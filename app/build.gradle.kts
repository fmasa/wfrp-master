plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
    kotlin("plugin.serialization")
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
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
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi" +
            "-Xopt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi" +
            "-P" +
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:navigation"))
    implementation(project(":app:compendium"))
    implementation(project(":app:inventory"))

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

    // Firebase Performance
//    implementation("com.google.firebase:firebase-perf-ktx:20.0.1")
}
