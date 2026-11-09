package com.re.reshell

import android.content.Context
import android.os.ParcelFileDescriptor
import androidx.annotation.Keep
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter

@Keep
class ShellService(@Suppress("UNUSED_PARAMETER") context: Context) : IShellService.Stub() {

    private val shell = InteractiveShell()

    override fun executeCommand(command: String): String = shell.execute(command)

    override fun executeCommandWithFd(command: String): ParcelFileDescriptor? = null

    override fun writeInput(data: String) {
        // 指令框模式用不到，留空即可
    }

    override fun destroy() {
        shell.close()
    }
}

private class InteractiveShell {
    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null
    private val lock = Any()

    private fun ensureStarted() {
        if (process?.isAlive == true) return
        val p = ProcessBuilder("sh")
            .redirectErrorStream(true)
            .start()
        process = p
        writer = BufferedWriter(OutputStreamWriter(p.outputStream))
        reader = p.inputStream.bufferedReader()
    }

    fun execute(command: String): String {
        synchronized(lock) {
            try {
                ensureStarted()
            } catch (e: Exception) {
                return "启动 shell 失败: ${e.message}"
            }

            val w = writer ?: return "shell 未启动"
            val r = reader ?: return "shell 未启动"
            val marker = "__DONE_${System.nanoTime()}__"

            try {
                w.write(command)
                w.newLine()
                w.write("echo $marker")
                w.newLine()
                w.flush()
            } catch (e: Exception) {
                close()
                return "写入失败: ${e.message}"
            }

            val sb = StringBuilder()
            try {
                while (true) {
                    val line = r.readLine() ?: break
                    if (line == marker) break
                    sb.append(line).append('\n')
                }
            } catch (e: Exception) {
                close()
                return "读取失败: ${e.message}"
            }

            return sb.toString().trimEnd('\n')
        }
    }

    fun close() {
        try { writer?.write("exit\n"); writer?.flush() } catch (_: Exception) {}
        try { process?.destroy() } catch (_: Exception) {}
        writer = null
        reader = null
        process = null
    }
}