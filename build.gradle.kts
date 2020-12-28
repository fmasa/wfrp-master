buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha03")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")

        // Firebase-related dependencies
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
        classpath("com.github.triplet.gradle:play-publisher:3.2.0-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/hotkeytlt/maven")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
