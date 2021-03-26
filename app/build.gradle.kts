import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "cz.frantisekmasa.dnd"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = System.getenv("SUPPLY_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("SUPPLY_VERSION_NAME") ?: "dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //
        // Firestore emulator setup
        //
        val properties = Properties()
        val propertiesFile = File("local.properties")

        if (propertiesFile.exists()) {
            properties.load(FileInputStream("local.properties"))
        }

        buildConfigField(
            "String",
            "FIRESTORE_EMULATOR_URL",
            "\"${properties.getOrDefault("dev.firestoreEmulatorUrl", "")}\""
        )
        //
        // End of Firestore Emulator setup
        //
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
            manifestPlaceholders(mapOf("analytics_activated" to "false"))
        }


        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "app_name", "WFRP Master")
            resValue("string", "character_ad_unit_id", "ca-app-pub-8647604386686373/9919978313")
            resValue("string", "game_master_ad_unit_id", "ca-app-pub-8647604386686373/7714574658")
            manifestPlaceholders(mapOf("analytics_activated" to "true"))
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
    }

    compileOptions {
        // Allow use of Java 8 APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = freeCompilerArgs +
                "-Xallow-jvm-ir-dependencies" +
                "-Xskip-prerelease-check" +
                "-Xopt-in=androidx.compose.foundation.layout.ExperimentalLayout" +
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi" +
                "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi" +
                "-P" +
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:navigation"))
    implementation(project(":app:compendium"))
    implementation(project(":app:combat"))
    implementation(project(":app:inventory"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Allow use of Java 8 APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("com.google.android.gms:play-services-auth:19.0.0")

    // Permission management
    implementation("com.sagar:coroutinespermission:2.0.3")

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
    implementation("com.vanpra.compose-material-dialogs:datetime:0.3.2")

    // Checking network access
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.4.2")

    // Firebase Performance
    implementation("com.google.firebase:firebase-perf-ktx:19.1.1")
}
