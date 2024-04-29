import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.moko.resources) apply false
}

subprojects {
    println(rootProject.libs.plugins.ktlint)
}
