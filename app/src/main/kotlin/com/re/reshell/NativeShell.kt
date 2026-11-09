package com.re.reshell

class NativeShell {
    init {
        System.loadLibrary("reshell")
    }

    // 接收命令和当前工作目录，返回原生字节输出
    external fun execCommand(cmd: String, cwd: String): ByteArray

    fun execute(command: String, cwd: String): String {
        val bytes = execCommand(command, cwd)
        // 处理换行符并去掉首尾空行
        return String(bytes, Charsets.UTF_8).replace("\r\n", "\n").replace("\r", "").trimEnd('\n')
    }
}