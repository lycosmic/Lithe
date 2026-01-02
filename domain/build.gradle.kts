plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // kotlin, url: https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    implementation(libs.kotlin.stdlib)
    // coroutines, url: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation(libs.kotlinx.coroutines.core)


    // javax inject, url: https://mvnrepository.com/artifact/javax.inject/javax.inject
    implementation(libs.javax.inject)
    // kotlin serialization, url: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-core
    implementation(libs.kotlinx.serialization.core)
}
