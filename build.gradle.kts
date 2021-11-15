buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.agp}")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.0.0")

        classpath(kotlin("gradle-plugin", Versions.kotlin))
        classpath(kotlin("serialization", Versions.kotlin))

        // Firebase-related dependencies
        classpath("com.google.gms:google-services:4.3.8")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        // classpath("com.google.firebase:perf-plugin:1.4.0")
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
