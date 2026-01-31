plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Room 需要
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.github.lycosmic.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(project(":domain"))
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.documentfile)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Jsoup: Body HTML for parsing Epub
    implementation(libs.jsoup)

    // GSON
    implementation(libs.gson)
}


// Room schema 导出配置 (KSP)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
