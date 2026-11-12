# ReShell

> 一款基于 Shizuku 的极简、高颜值 Android 本地 Shell 工具。

**ReShell** 是一个开源的 Android 终端指令执行工具。它结合了 HyperOS 风格的现代 UI 与底层的 C++ PTY 技术，让你无需连接电脑，即可在手机上执行 ADB 级别的高级指令。

## ✨ 功能特性

- 🎨 **Miuix 风格 UI**：基于 Jetpack Compose 与 Miuix 组件库构建，带来丝滑的 HyperOS 视觉与交互体验。
- ⚡ **Shizuku 特权支持**：无缝对接 Shizuku API，轻松获取 `Shell`、`System` 甚至 `Root` 权限。
- 🔄 **本地降级模式**：当 Shizuku 未激活或未授权时，自动降级为本地普通应用权限，依然可以执行常规文件操作。
- 🚀 **底层 C++ PTY**：利用 NDK (JNI) 调用 `forkpty`，提供真实的伪终端环境，完美支持 `ls` 等命令的彩色输出（ANSI 转义序列解析）。
- 👑 **智能 SU 提权**：原生支持 `su root`、`su system` 等身份切换，提权后输入框前缀自动切换为 `#`。
- 📂 **Termux 风格路径**：自动缩短过长的绝对路径（如 `~ $`），界面更清爽。
- 📌 **快捷指令收藏**：支持一键保存常用 Shell 指令，下次点击直接执行。
- 📝 **纯净日志与复制**：支持长按选中并复制日志输出。

## 🛠️ 技术栈

- **语言**：Kotlin + C++
- **UI 框架**：Jetpack Compose + [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix)
- **跨进程通信**：AIDL (Shizuku UserService)
- **底层 Native**：Android NDK / CMake (基于 `forkpty` 实现终端)
- **架构**：MVVM + 协程 (Coroutines)

## 🚀 编译与构建

### 环境要求
- **Android Studio**（推荐最新版）或 **AndroidIDE**（移动端编译）
- **JDK 17**
- **Android NDK (r28c 或更高)**
- **CMake 4.1.2**

### 本地编译步骤
1. 克隆本仓库到本地：
   ```bash
   git clone https://github.com/thww488/ReShell.git
2. 使用 Android Studio 打开项目，等待 Gradle Sync 完成。
3. 确保你的 SDK Manager 中已安装上述指定版本的 NDK 与 CMake 组件。
4. 直接点击 Run 构建并安装到设备。

📖 使用指南

1. 确保手机已安装并激活了 Shizuku。
2. 打开 ReShell，根据提示授予 Shizuku 权限。
3. 在指令框输入 Shell 指令（如 pm list packages、top -n 1）。
4. 如果需要 Root 权限，直接输入 su 即可（需手机已 Root 并安装了 Root 管理器）。

💖 开源致谢

ReShell 的诞生离不开以下优秀的开源项目

· Shizuku
· Miuix
· AndroidX
· Kotlin

📄 开源协议

本项目基于 Apache License 2.0 协议开源。

---

Developed with ❤️ by Northwing_thww