buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0")

        // Firebase-related dependencies
        classpath("com.google.gms:google-services:4.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.2.0")
        classpath("com.github.triplet.gradle:play-publisher:3.0.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
