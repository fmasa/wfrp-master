
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
    jvm()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.animation.ExperimentalAnimationApi")
                optIn("com.google.accompanist.permissions.ExperimentalPermissionsApi")
            }
        }

        val ktorVersion = "2.0.0"
        val kodeinVersion = "7.11.0"

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)

                val voyagerVersion = "1.0.0-rc04"
                api("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                api("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

                api(project(":common:firebase"))

                api("io.ktor:ktor-client-core:$ktorVersion")

                // Basic Kotlin stuff
                api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

                // Dependency injection 2
                api("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")
                implementation("org.kodein.di:kodein-di:$kodeinVersion")

                implementation("io.arrow-kt:arrow-core:1.0.1")

                // Parser combinator library (grammars etc.)
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.2")

                // Multiplatform UUID
                implementation("com.benasher44:uuid:0.3.1")

                // JSON encoding
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                // Logging
                api("io.github.aakira:napier:${Versions.napier}")

                // HTTP client
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                api("io.ktor:ktor-client-cio:$ktorVersion")

                val richtextVersion = "0.13.0"
                implementation("com.halilibo.compose-richtext:richtext-commonmark:$richtextVersion")
                implementation("com.halilibo.compose-richtext:richtext-ui-material:$richtextVersion")
                implementation("io.github.z4kn4fein:semver:1.3.3")

                implementation("org.jsoup:jsoup:1.15.3")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                // Permission management
                implementation("com.google.accompanist:accompanist-permissions:0.20.0")

                api("androidx.activity:activity-compose:1.5.0-alpha03")

                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.3.1")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")

                implementation("org.kodein.di:kodein-di-framework-android-core:$kodeinVersion")

                api("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

                // Authentication
                api("com.google.android.gms:play-services-auth:19.0.0")
                implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
                implementation("com.google.firebase:firebase-dynamic-links-ktx:21.0.1")

                // Shared Preferences DataStore
                api("androidx.datastore:datastore-preferences:1.0.0")

                // Firebase functions
                api("com.google.firebase:firebase-functions-ktx:20.0.1")

                implementation("com.tom-roush:pdfbox-android:2.0.27.0")

                // Coil - image library
                implementation("io.coil-kt:coil-compose:2.0.0")

                // Time picker
                implementation("io.github.vanpra.compose-material-dialogs:datetime:0.5.1")

                // QR codes
                implementation("com.google.zxing:core:3.3.3")

                implementation("androidx.camera:camera-camera2:1.1.0-alpha02")
                implementation("androidx.camera:camera-core:1.1.0-alpha02")
                implementation("androidx.camera:camera-lifecycle:1.1.0-alpha02")
                implementation("androidx.camera:camera-view:1.0.0-alpha22")

                // Network availability check
                implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.4.2")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)

                implementation("com.soywiz.korlibs.korau:korau:2.2.0")
                implementation("org.apache.pdfbox:pdfbox:2.0.27")
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

    dependencies {
        // Allow use of Java 8 APIs on older Android versions
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    }

    compileOptions {
        // Allow use of Java 8 APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
