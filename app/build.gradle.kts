plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.google.hilt)
    alias(libs.plugins.google.ksp)
    // Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.github.lycosmic.lithe"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.lycosmic.lithe"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(libs.androidx.material3)
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

// Room schema 导出配置 (KSP)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
