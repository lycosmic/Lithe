import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.google.hilt)
    alias(libs.plugins.google.ksp)
    // Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization)
}

// --- 加载签名配置函数 ---
val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "io.github.lycosmic.lithe"
    compileSdk {
        version = release(36)
    }

    // --- 签名配置 ---
    signingConfigs {
        create("release") {
            if (keystoreProperties.containsKey("store.file")) {
                storeFile = file(keystoreProperties["store.file"] as String)
                storePassword = keystoreProperties["store.password"] as String
                keyAlias = keystoreProperties["key.alias"] as String
                keyPassword = keystoreProperties["key.password"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "io.github.lycosmic.lithe"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- APK 自动重命名 ---
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            // 为 release 构建类型设置输出文件名
            applicationVariants.all {
                if (buildType.name == "release") {
                    outputs.all {
                        val appName = "Lithe"
                        val version = versionName
                        val date = SimpleDateFormat("yyyyMMdd").format(Date())
                        val outputFileName = "${appName}_v${version}_${date}_release.apk"

                        (this as ApkVariantOutputImpl).outputFileName = outputFileName
                    }
                }
            }
        }

        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true // 添加 BuildConfig
    }
}

dependencies {
    // Domain和Data模块的依赖
    implementation(project(":domain"))
    implementation(project(":data"))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // Navigation3 Adaptive
    implementation(libs.androidx.compose.adaptive.navigation3)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.core)

    // Navigation3 ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Material Icons Extended
    implementation(libs.androidx.compose.material.icons.extended)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Timber
    implementation(libs.timber)

    // Drag & Drop, url: https://github.com/Calvin-LL/Reorderable
    implementation(libs.reorderable)

    // LazyColumnScrollbar, url: https://github.com/nanihadesuka/LazyColumnScrollbar
    implementation(libs.lazycolumnscrollbar)

    // Material3
    implementation(libs.androidx.compose.material3.v150alpha06)

    // ---------------- 初始依赖 ----------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}