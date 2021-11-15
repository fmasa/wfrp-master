plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    // id("com.google.firebase.firebase-perf")
}

android {
    compileSdk = Versions.Android.compileSdk

    defaultConfig {
        applicationId = "cz.frantisekmasa.dnd"
        minSdk = Versions.Android.minSdk
        targetSdk = Versions.Android.targetSdk
        versionCode = System.getenv("SUPPLY_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("SUPPLY_VERSION_NAME") ?: "dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = File(System.getProperty("user.dir") + "/app/.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "uploadKey"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "[Debug] WFRP Master")
            resValue("string", "character_ad_unit_id", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "game_master_ad_unit_id", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "combat_ad_unit_id", "ca-app-pub-3940256099942544/6300978111")

            addManifestPlaceholders(
                mapOf(
                    "analytics_activated" to "false",
                    "usesCleartextTraffic" to "true",
                )
            )
        }


        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")

            resValue("string", "app_name", "WFRP Master")
            resValue("string", "character_ad_unit_id", "ca-app-pub-8647604386686373/9919978313")
            resValue("string", "game_master_ad_unit_id", "ca-app-pub-8647604386686373/7714574658")
            resValue("string", "combat_ad_unit_id", "ca-app-pub-8647604386686373/3858132571")

            addManifestPlaceholders(
                mapOf(
                    "analytics_activated" to "true",
                    "usesCleartextTraffic" to "false",
                )
            )
        }
    }

    compileOptions {
        // Allow use of Java 8 APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs +
            "-Xskip-prerelease-check" +
            "-Xopt-in=androidx.compose.foundation.layout.ExperimentalLayout" +
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi" +
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi" +
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi" +
            "-Xopt-in=androidx.compose.animation.ExperimentalFoundationApi" +
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi" +
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi" +
            "-Xopt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi" +
            "-P" +
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    implementation(project(":common"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Allow use of Java 8 APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("com.google.android.gms:play-services-auth:19.0.0")

    // Permission management
    implementation("com.google.accompanist:accompanist-permissions:0.20.0")

    // QR code scanning
    implementation("com.google.zxing:core:3.3.3")
    implementation("androidx.camera:camera-camera2:1.1.0-alpha02")
    implementation("androidx.camera:camera-core:1.1.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha02")
    implementation("androidx.camera:camera-view:1.0.0-alpha22")

    // Testing utilities
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("org.mockito:mockito-core:2.7.22")

    // Time picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.5.1")

    // Checking network access
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.4.2")

    // Tools for Rulebook PDF parsing
    implementation("com.github.librepdf:openpdf:1.3.25")
    implementation("com.github.andob:android-awt:1.0.0")

    // Navigation
    api("androidx.navigation:navigation-compose:2.4.0-beta02")

    // Basic Android stuff
    api("androidx.core:core-ktx:1.5.0")
    api("androidx.fragment:fragment-ktx:1.3.5")

    // Jetpack Compose
    api("androidx.compose.ui:ui:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling:${Versions.compose}")
    api("androidx.activity:activity-compose:1.3.0-beta02")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")

    // Firebase-related dependencies
    api(platform("com.google.firebase:firebase-bom:28.4.2"))
    api("com.google.firebase:firebase-firestore-ktx")
    api("com.google.firebase:firebase-analytics-ktx")
    api("com.google.firebase:firebase-auth-ktx")
    api("com.google.firebase:firebase-dynamic-links-ktx")
    api("com.google.firebase:firebase-functions-ktx")
    api("androidx.work:work-runtime-ktx:2.7.0")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")

    api("io.arrow-kt:arrow-core:0.10.4")

    // Parser combinator library (grammars etc.)
    api("com.github.h0tk3y.betterParse:better-parse:0.4.2")

    // JSON encoding
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // HTTP Client
    val ktorVersion = "1.6.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    // Firebase Performance
//    implementation("com.google.firebase:firebase-perf-ktx:20.0.1")
}
