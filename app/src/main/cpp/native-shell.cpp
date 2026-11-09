#include <jni.h>
#include <unistd.h>
#include <pty.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <termios.h>
#include <android/log.h>
#include <string>
#include <cstdlib>

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_re_reshell_NativeShell_execCommand(JNIEnv *env, jobject /* this */, jstring cmd, jstring cwd) {
    const char *c_cmd = env->GetStringUTFChars(cmd, nullptr);
    const char *c_cwd = env->GetStringUTFChars(cwd, nullptr);

    int masterFd;
    pid_t pid = forkpty(&masterFd, nullptr, nullptr, nullptr);

    if (pid == 0) {
        // 子进程：切换目录并执行命令
        if (c_cwd && strlen(c_cwd) > 0) {
            chdir(c_cwd);
        }
        setenv("TERM", "xterm-256color", 1);
        // 使用 sh -c 执行单次命令，不进入交互模式
        execl("/system/bin/sh", "sh", "-c", c_cmd, nullptr);
        _exit(1);
    }

    // 父进程：读取输出
    std::string output;
    char buffer[4096];
    while (true) {
        ssize_t bytesRead = read(masterFd, buffer, sizeof(buffer));
        if (bytesRead <= 0) break;
        output.append(buffer, bytesRead);
    }
    
    close(masterFd);
    waitpid(pid, nullptr, 0);

    env->ReleaseStringUTFChars(cmd, c_cmd);
    env->ReleaseStringUTFChars(cwd, c_cwd);

    jbyteArray result = env->NewByteArray(output.size());
    env->SetByteArrayRegion(result, 0, output.size(), (const jbyte*)output.data());
    return result;
}