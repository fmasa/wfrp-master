import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    android()
    jvm()

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation("io.github.aakira:napier:${Versions.napier}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
            }
        }
        named("jvmMain") {
            dependencies {
                api("com.google.cloud:google-cloud-firestore:3.0.18")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
            }
        }
        named("androidMain") {
            dependencies {
                api("com.google.firebase:firebase-auth-ktx:21.0.2")
                api("com.google.firebase:firebase-crashlytics-ktx:18.2.9")
                api("com.google.firebase:firebase-firestore-ktx:24.0.2")
                api("com.google.firebase:firebase-analytics-ktx:20.1.0")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0")
            }
        }
    }
}


android {
    compileSdk = Versions.Android.compileSdk

    defaultConfig {
        minSdk = Versions.Android.minSdk
        targetSdk = Versions.Android.targetSdk

        //
        // Firestore emulator setup
        //
        val properties = if (File("local.properties").exists())
            loadProperties("local.properties")
        else Properties()

        buildConfigField(
            "String",
            "FUNCTIONS_EMULATOR_URL",
            "\"${properties.getOrDefault("dev.functionsEmulatorUrl", "")}\""
        )

        buildConfigField(
            "String",
            "FIRESTORE_EMULATOR_URL",
            "\"${properties.getOrDefault("dev.firestoreEmulatorUrl", "")}\""
        )
        //
        // End of Firestore Emulator setup
        //
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        }
    }
}

