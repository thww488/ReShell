import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.re.reshell"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.re.reshell"
        minSdk = 28
        targetSdk = 37
        versionCode = 1200
        versionName = "1.2.0"
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }
    signingConfigs {
    val props = Properties().apply {
        val f = rootProject.file("local.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
    create("keystore") {
        storeFile = file(props.getProperty("RELEASE_STORE_FILE") ?: "xxx.jks")
        storePassword = props.getProperty("RELEASE_STORE_PASSWORD") ?: "xxx"
        keyAlias = props.getProperty("RELEASE_KEY_ALIAS") ?: "xxx"
        keyPassword = props.getProperty("RELEASE_KEY_PASSWORD") ?: "xxx"
    }
    }
    buildTypes {
        release {
            isMinifyEnabled = true // 按你之前的调试状态保留
            signingConfig = signingConfigs.getByName("keystore") // ← 关键：应用签名配置
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
        aidl = true
        buildConfig = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("17"))
        }
    }
}

dependencies {
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.material:material-icons-core:1.7.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("top.yukonga.miuix.kmp:miuix-ui:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-icons:0.9.4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
}