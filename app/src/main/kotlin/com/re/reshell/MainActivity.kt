package com.re.reshell

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE = 1001
    }

    private lateinit var shellManager: ShizukuShellManager
    private val nativeShell = NativeShell()
    
    private var isReady by mutableStateOf(false)
    private var useShizuku by mutableStateOf(false)
    private var isRoot by mutableStateOf(false)
    private var privilegeSource by mutableStateOf("正在初始化...")
    
    private var baseUser by mutableStateOf("")
    private var currentUser by mutableStateOf("")
    
    private var currentDir by mutableStateOf("/")
    private var previousDir by mutableStateOf("") 
    
    private var showSettings by mutableStateOf(false)
    private var showAbout by mutableStateOf(false)
    private var enableShizuku by mutableStateOf(true)
    private var defaultDir by mutableStateOf("")
    
    private val outputs = mutableStateListOf<String>()
    private val prefs by lazy { getSharedPreferences("reshell_prefs", Context.MODE_PRIVATE) }
    private val bookmarks = mutableStateListOf<String>()

    private val permissionListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == REQUEST_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) connectShizukuShell() else startLocalFallback()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shellManager = ShizukuShellManager(this)

        enableShizuku = prefs.getBoolean("enable_shizuku", true)
        defaultDir = prefs.getString("default_dir", "${filesDir.absolutePath}/home") ?: "${filesDir.absolutePath}/home"
        prefs.getStringSet("bookmarks", emptySet())?.forEach { bookmarks.add(it) }

        setContent {
            MiuixTheme {
                if (showAbout) {
                    AboutScreen(onBack = { showAbout = false })
                } else if (showSettings) {
                    SettingsScreen(
                        isShizukuEnabled = enableShizuku,
                        currentDefaultDir = defaultDir,
                        onBack = { showSettings = false },
                        onShizukuToggle = { enabled ->
                            enableShizuku = enabled
                            prefs.edit().putBoolean("enable_shizuku", enabled).apply()
                            if (enabled) checkShizuku() else disconnectShizuku()
                        },
                        onUpdateDir = { newDir ->
                            defaultDir = newDir
                            prefs.edit().putString("default_dir", newDir).apply()
                            lifecycleScope.launch(Dispatchers.IO) {
                                val cmd = "mkdir -p $newDir"
                                if (useShizuku) shellManager.execute("cd / && $cmd") else nativeShell.execute(cmd, "/")
                                withContext(Dispatchers.Main) { currentDir = newDir }
                            }
                        },
                        onOpenAbout = { showAbout = true }
                    )
                } else {
                    MainScreen(
                        isConnected = isReady,
                        isRoot = isRoot,
                        privilegeSource = privilegeSource,
                        currentUser = currentUser,
                        outputs = outputs,
                        bookmarks = bookmarks,
                        onExecute = ::executeCommand,
                        onReconnect = ::checkShizuku,
                        onClear = { outputs.clear() },
                        onAddBookmark = ::addBookmark,
                        onRemoveBookmark = ::removeBookmark,
                        onOpenSettings = { showSettings = true }
                    )
                }
            }
        }

        if (enableShizuku) checkShizuku() else {
            startLocalFallback()
            outputs.add(getString(R.string.log_shizuku_disabled))
        }
    }

    override fun onStart() { super.onStart(); Shizuku.addRequestPermissionResultListener(permissionListener) }
    override fun onStop() { Shizuku.removeRequestPermissionResultListener(permissionListener); super.onStop() }
    override fun onDestroy() {
        try { shellManager.disconnect() } catch (_: Exception) {}
        super.onDestroy()
    }

    private fun disconnectShizuku() {
        lifecycleScope.launch(Dispatchers.IO) {
            try { shellManager.disconnect() } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                startLocalFallback()
                outputs.add(getString(R.string.log_shizuku_closed))
            }
        }
    }

    private fun checkShizuku() {
        if (!enableShizuku) return
        isReady = false
        privilegeSource = "正在连接..."
        isRoot = false
        
        lifecycleScope.launch(Dispatchers.IO) {
            try { shellManager.disconnect() } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                try {
                    if (!Shizuku.pingBinder()) {
                        outputs.add(getString(R.string.log_shizuku_not_running))
                        startLocalFallback()
                        return@withContext
                    }
                    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) connectShizukuShell() else Shizuku.requestPermission(REQUEST_CODE)
                } catch (e: Exception) { 
                    outputs.add(getString(R.string.log_shizuku_error_fallback, e.message ?: ""))
                    startLocalFallback() 
                }
            }
        }
    }

    private fun connectShizukuShell() {
        lifecycleScope.launch {
            try {
                shellManager.connect()
                useShizuku = true
                isReady = true
                privilegeSource = "Shizuku"
                outputs.add(getString(R.string.log_shizuku_connected))
                
                val initUser = withContext(Dispatchers.IO) {
                    try { shellManager.execute("whoami") } catch (e: Exception) { "" }
                }.trim().lines().lastOrNull()?.trim() ?: "shell"
                
                baseUser = initUser
                currentUser = initUser
                isRoot = (initUser == "root")
                
                withContext(Dispatchers.IO) { shellManager.execute("mkdir -p $defaultDir") }
                currentDir = defaultDir
                
            } catch (e: Exception) { 
                outputs.add(getString(R.string.log_shizuku_conn_fail, e.message ?: ""))
                startLocalFallback() 
            }
        }
    }

    private fun startLocalFallback() {
        useShizuku = false
        isReady = true
        privilegeSource = "本地"
        // 核心修复：使用多语言资源
        baseUser = getString(R.string.local_user)
        currentUser = getString(R.string.local_user)
        isRoot = false
        
        lifecycleScope.launch(Dispatchers.IO) {
            nativeShell.execute("mkdir -p $defaultDir", "/")
            withContext(Dispatchers.Main) { currentDir = defaultDir }
        }
    }

    private fun executeCommand(command: String) {
        if (!isReady) {
            outputs.add("$ $command")
            outputs.add(getString(R.string.log_shell_not_ready))
            return
        }

        var finalCmd = command.trim()
        if (finalCmd.startsWith("adb shell ")) finalCmd = finalCmd.substring(10)
        else if (finalCmd.startsWith("adb ")) finalCmd = finalCmd.substring(4)
        if (finalCmd.isBlank()) return

        val shortPath = when {
            currentDir == defaultDir -> "~"
            currentDir.startsWith("$defaultDir/") -> "~/" + currentDir.substringAfter("$defaultDir/")
            currentDir == "/" -> "/"
            else -> currentDir.substringAfterLast("/")
        }
        val promptPrefix = "$shortPath ${if (isRoot) "#" else "$"}"
        outputs.add("$promptPrefix $command")

        lifecycleScope.launch {
            val parts = finalCmd.split("\\s+".toRegex())
            val isSuCmd = parts[0] == "su"
            val isCdCmd = parts[0] == "cd"

            if (finalCmd.startsWith("ls") && !finalCmd.contains("--color")) {
                finalCmd = finalCmd.replaceFirst("ls", "ls --color=always")
            }

            if (isCdCmd) {
                var target = if (parts.size > 1) parts[1] else "~"
                if (target.startsWith("~")) target = target.replaceFirst("~", defaultDir)
                if (target == "-") {
                    if (previousDir.isNotBlank()) target = previousDir
                    else {
                        outputs.add("cd: OLDPWD not set")
                        return@launch
                    }
                }
                if (target.isBlank()) target = defaultDir

                val actualCmd = "cd \"$target\" && pwd"
                val res = withContext(Dispatchers.IO) {
                    if (useShizuku) shellManager.execute(actualCmd) else nativeShell.execute(actualCmd, currentDir)
                }
                
                val lines = res.trim().lines()
                val newPath = lines.lastOrNull { it.startsWith("/") }

                if (newPath != null) {
                    previousDir = currentDir
                    currentDir = newPath
                } else {
                    val errRes = withContext(Dispatchers.IO) {
                        if (useShizuku) shellManager.execute("cd \"$target\"") else nativeShell.execute("cd \"$target\"", currentDir)
                    }
                    if (errRes.isNotBlank()) outputs.add(errRes)
                }
                return@launch
            }

            if (isSuCmd) {
                if (currentUser != baseUser) {
                    currentUser = baseUser
                    isRoot = (currentUser == "root")
                    privilegeSource = if (useShizuku) "Shizuku" else "本地"
                    outputs.add(getString(R.string.log_su_exited))
                }

                if (finalCmd.contains("-c")) {
                    val result = withContext(Dispatchers.IO) {
                        if (useShizuku) shellManager.execute(finalCmd) else nativeShell.execute(finalCmd, currentDir)
                    }
                    if (result.isNotBlank()) outputs.add(result)
                    return@launch
                }

                var targetUser = if (parts.size > 1) parts[1] else "root"
                outputs.add(getString(R.string.log_request_su, targetUser))
                
                val testCmd = "su $targetUser -c whoami"
                val testResult = withContext(Dispatchers.IO) {
                    if (useShizuku) shellManager.execute(testCmd) else nativeShell.execute(testCmd, currentDir)
                }
                
                if (testResult.isNotBlank() && !testResult.contains("not found") && !testResult.contains("Permission denied")) {
                    val realUser = testResult.trim().lines().lastOrNull { it.isNotBlank() }?.trim() ?: targetUser
                    currentUser = realUser
                    isRoot = (currentUser == "root")
                    privilegeSource = "SU"
                    outputs.add(getString(R.string.log_su_connected, currentUser))
                } else {
                    outputs.add(getString(R.string.log_su_failed))
                }
                return@launch
            }

            if (finalCmd == "exit") {
                if (currentUser != baseUser) {
                    currentUser = baseUser
                    isRoot = (currentUser == "root")
                    privilegeSource = if (useShizuku) "Shizuku" else "本地"
                    outputs.add(getString(R.string.log_reverted_privilege, currentUser))
                } else {
                    outputs.add(getString(R.string.log_base_privilege))
                }
                return@launch
            }

            var actualCmd = finalCmd
            if (currentUser != baseUser && currentUser != getString(R.string.local_user)) {
                val escapedCmd = finalCmd.replace("\"", "\\\"")
                actualCmd = "su $currentUser -c \"$escapedCmd\""
            }

            val result = withContext(Dispatchers.IO) {
                try {
                    if (useShizuku) shellManager.execute(actualCmd) else nativeShell.execute(actualCmd, currentDir)
                } catch (e: Exception) { 
                    getString(R.string.log_exec_error, e.message ?: "") 
                }
            }
            if (result.isNotBlank()) outputs.add(result)
        }
    }

    private fun addBookmark(cmd: String) {
        if (cmd.isBlank() || bookmarks.contains(cmd)) return
        bookmarks.add(cmd)
        prefs.edit().putStringSet("bookmarks", bookmarks.toSet()).apply()
        outputs.add(getString(R.string.log_added_bookmark, cmd))
    }

    private fun removeBookmark(cmd: String) {
        bookmarks.remove(cmd)
        prefs.edit().putStringSet("bookmarks", bookmarks.toSet()).apply()
        outputs.add(getString(R.string.log_removed_bookmark, cmd))
    }
}