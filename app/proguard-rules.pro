# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 保护 AIDL 接口及其生成的 Stub/Proxy（防止构造函数被 R8 移除）
-keep class com.re.reshell.IShellService { *; }
-keep class com.re.reshell.IShellService$Stub { *; }
-keep class com.re.reshell.IShellService$Stub$Proxy { *; }

# 保护 UserService 实现类（Shizuku 通过反射实例化）
-keep class com.re.reshell.ShellService { *; }

# 保护 Shizuku 框架类（防止反射调用失败）
-keep class rikka.shizuku.** { *; }