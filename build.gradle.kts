buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.compose:compose-gradle-plugin:${Versions.compose}")

        classpath(kotlin("gradle-plugin", Versions.kotlin))
        classpath(kotlin("serialization", Versions.kotlin))

        classpath("dev.icerock.moko:resources-generator:0.23.0")

        // Firebase-related dependencies
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        // classpath("com.google.firebase:perf-plugin:1.4.0")
    }

    repositories {
        gradlePluginPortal()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    kotlin("plugin.serialization") version Versions.kotlin
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
