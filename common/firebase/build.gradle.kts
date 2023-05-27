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
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        named("jvmMain") {
            dependencies {
                api("com.google.cloud:google-cloud-firestore:3.0.18")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
                api("io.ktor:ktor-client-cio:${Versions.ktor}")
                api("io.ktor:ktor-client-core:${Versions.ktor}")
            }
        }
        named("androidMain") {
            dependencies {
                api("com.google.firebase:firebase-auth-ktx:21.2.0")
                api("com.google.firebase:firebase-crashlytics-ktx:18.3.6")
                api("com.google.firebase:firebase-firestore-ktx:24.4.5")
                api("com.google.firebase:firebase-analytics-ktx:21.2.1")
                api("com.google.firebase:firebase-functions-ktx:20.2.2")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
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
