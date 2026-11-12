# ReShell

[English](README.md) | [简体中文](README.zh-CN.md)

> A minimalist, elegant Android local Shell tool based on Shizuku.

**ReShell** is an open-source Android terminal command execution tool. It combines a HyperOS-style modern UI with underlying C++ PTY technology, allowing you to execute ADB-level advanced commands directly on your phone without connecting to a computer.

## ✨ Features

- 🎨 **Miuix Style UI**: Built with Jetpack Compose and the Miuix component library, delivering a smooth HyperOS visual and interactive experience.
- ⚡ **Shizuku Privilege Support**: Seamlessly integrates with the Shizuku API, easily obtaining `Shell`, `System`, or even `Root` privileges.
- 🔄 **Local Fallback Mode**: When Shizuku is not activated or authorized, it automatically falls back to local normal app privileges, still capable of executing regular file operations.
- 🚀 **Underlying C++ PTY**: Utilizes NDK (JNI) to call `forkpty`, providing a real pseudo-terminal environment, perfectly supporting colored output (ANSI escape sequence parsing) for commands like `ls`.
- 👑 **Smart SU Escalation**: Natively supports identity switching like `su root`, `su system`, etc. The input box prefix automatically switches to `#` after privilege escalation.
- 📂 **Termux-Style Path**: Automatically shortens overly long absolute paths (e.g., `~ $`), keeping the interface clean and refreshing.
- 📌 **Command Bookmarks**: Supports one-click saving of frequently used Shell commands, allowing direct execution on the next click.
- 📝 **Pure Logs & Copy**: Supports long-press to select and copy log outputs.

## 🛠️ Tech Stack

- **Languages**: Kotlin + C++
- **UI Framework**: Jetpack Compose + [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix)
- **Inter-process Communication**: AIDL (Shizuku UserService)
- **Underlying Native**: Android NDK / CMake (Terminal implemented based on `forkpty`)
- **Architecture**: MVVM + Coroutines

## 🚀 Compilation & Build

### Environment Requirements
- **Android Studio** (latest version recommended) or **AndroidIDE** (for on-device compilation)
- **JDK 17**
- **Android NDK (r28c or higher)**
- **CMake 4.1.2**

### Local Build Steps
1. Clone this repository to your local machine:
   ```bash
   git clone https://github.com/thww488/ReShell.git

2. Open the project with Android Studio and wait for Gradle Sync to complete.
3. Ensure that the specified versions of NDK and CMake components are installed in your SDK Manager.
4. Simply click Run to build and install it on your device.

📖 Usage Guide

1. Ensure Shizuku is installed and activated on your phone.
2. Open ReShell and grant Shizuku permissions as prompted.
3. Enter Shell commands in the command box (e.g., pm list packages, top -n 1).
4. If you need Root privileges, simply enter su (your phone must be rooted and have Root Manager installed).

💖 Open Source Acknowledgments

The birth of ReShell would not have been possible without the following excellent open-source projects:

· Shizuku
· Miuix
· AndroidX
· Kotlin

📄 License

This project is open-sourced under the Apache License 2.0.

---

Developed with ❤️ by Northwing_thww