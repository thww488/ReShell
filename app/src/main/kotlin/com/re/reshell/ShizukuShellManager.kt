package com.re.reshell

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import rikka.shizuku.Shizuku

class ShizukuShellManager(
    private val context: Context
) {
    private var shellService: IShellService? = null
    private var boundConnection: ServiceConnection? = null

    private val userServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(context.packageName, ShellService::class.java.name)
    )
        .daemon(false)
        .tag("shell_service_v1")
        .processNameSuffix("shell_service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)

    suspend fun connect(): IShellService {
        boundConnection?.let {
            Shizuku.unbindUserService(userServiceArgs, it, true)
            boundConnection = null
            shellService = null
        }

        val deferred = CompletableDeferred<IShellService>()
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val svc = IShellService.Stub.asInterface(binder)
                shellService = svc
                if (!deferred.isCompleted) deferred.complete(svc)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                shellService = null
                if (!deferred.isCompleted) {
                    deferred.completeExceptionally(IllegalStateException("服务连接断开"))
                }
            }
        }

        boundConnection = conn

        try {
            Shizuku.bindUserService(userServiceArgs, conn)
        } catch (e: Exception) {
            deferred.completeExceptionally(e)
        }

        return try {
            withTimeout(5_000) { deferred.await() }
        } catch (e: Exception) {
            Shizuku.unbindUserService(userServiceArgs, conn, true)
            boundConnection = null
            throw e
        }
    }

    fun execute(command: String): String {
    val svc = shellService ?: return "[Shell 未连接]"
    return try {
        svc.executeCommand(command)
    } catch (e: Exception) {
        "[执行异常: ${e.message}]"
    }
    }

    fun disconnect() {
        boundConnection?.let {
            Shizuku.unbindUserService(userServiceArgs, it, true)
        }
        boundConnection = null
        shellService = null
    }
}