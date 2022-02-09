import org.jetbrains.compose.compose
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("org.jetbrains.compose")
}

kotlin {
    android()
    sourceSets {
        val koinVersion = "3.1.2"

        all {
            languageSettings.apply {
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }

        val ktorVersion = "1.6.7"
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                // Basic Kotlin stuff
                api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

                // Dependency injection
                api("io.insert-koin:koin-android:$koinVersion")

                implementation("io.arrow-kt:arrow-core:1.0.1")

                // Parser combinator library (grammars etc.)
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.2")

                // Multiplatform UUID
                implementation("com.benasher44:uuid:0.3.1")

                // JSON encoding
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                // Logging
                api("io.github.aakira:napier:2.1.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.3.1")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")


                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")

                api("io.insert-koin:koin-android:$koinVersion")
                api("com.google.firebase:firebase-auth-ktx:21.0.1")
                api("com.google.firebase:firebase-crashlytics-ktx:18.2.4")
                api("com.google.firebase:firebase-firestore-ktx:24.0.0")
                api("com.google.firebase:firebase-analytics-ktx:20.0.1")

                api("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

                // Shared Preferences DataStore
                implementation("androidx.datastore:datastore-preferences:1.0.0")
                implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
                api("com.google.firebase:firebase-functions-ktx:20.0.1")

                // Coil - image library
                implementation("io.coil-kt:coil-compose:1.3.2")

                // Ads
                api("com.google.android.gms:play-services-ads:20.4.0")

                // Premium
                // TODO: Make implementation only
                api("com.revenuecat.purchases:purchases:4.0.2")
            }
        }

        val androidTest by getting {
            dependsOn(commonTest)
            dependencies {
                dependsOn(sourceSets.getByName("commonTest"))
                implementation(kotlin("test-junit"))
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
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
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.2.4")
}
