import java.io.File
import java.io.FileInputStream
import java.util.*
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import com.github.triplet.gradle.androidpublisher.ResolutionStrategy
import com.github.triplet.gradle.play.PlayPublisherExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")

    id("com.github.triplet.play") apply false
}

val playStoreJsonFile = File("./play_store_credentials.json")

if (playStoreJsonFile.exists()) {
    apply(plugin = "com.github.triplet.play")
    configure<PlayPublisherExtension> {
        defaultToAppBundles.set(true)
        track.set(System.getenv("PLAY_STORE_TRACK"))
        serviceAccountCredentials.set(playStoreJsonFile)
        resolutionStrategy.set(ResolutionStrategy.AUTO)
    }
}

val composeVersion = "1.0.0-alpha05"

android {

    lintOptions {
        disable("InvalidFragmentVersionForActivityResult") // This is temporary until we drop authentication fragment
    }

    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "cz.frantisekmasa.dnd"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = System.getenv("APP_VERSION_NAME") ?: "dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //
        // Firestore emulator setup
        //
        val properties = Properties()
        val propertiesFile = File("local.properties");

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
            storeFile = file(".keystore")
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
            manifestPlaceholders(mapOf("analytics_deactivated" to "true"))
        }


        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "app_name", "WFRP Master")
            resValue("string", "character_ad_unit_id", "ca-app-pub-8647604386686373/9919978313")
            resValue("string", "game_master_ad_unit_id", "ca-app-pub-8647604386686373/7714574658")
            manifestPlaceholders(mapOf("analytics_deactivated" to "false"))
        }
    }

    buildFeatures {
        compose = true
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-rc1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.wdullaer:materialdatetimepicker:4.2.3") {
        exclude(group = "androidx.appcompat")
        exclude(group = "androidx.recyclerview")
    }

    // Allow use of Java 8 APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")

    // Time picker dialog
    implementation("com.vanpra.compose-material-dialogs:datetime:0.2.4")

    // Koin
    implementation("org.koin:koin-android:2.1.5")
    implementation("org.koin:koin-android-viewmodel:2.1.5")
    implementation("org.koin:koin-androidx-fragment:2.1.5")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.ui:ui-tooling:$composeVersion")

    // Navigation
    implementation("com.github.zsoltk:compose-router:0.20.0")
    implementation("com.zachklipp:compose-backstack:0.7.0+alpha04") {
        exclude(group = "androidx.compose")
        exclude(group = "androidx.ui")
    }

    // Shared Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha02")

    // Firebase-related dependencies
    implementation("com.google.firebase:firebase-analytics:17.4.4")
    implementation("com.firebaseui:firebase-ui-auth:6.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:21.5.0")
    implementation("com.google.firebase:firebase-analytics-ktx:17.4.4")
    implementation("com.google.firebase:firebase-crashlytics:17.1.1")
    implementation("com.google.firebase:firebase-dynamic-links-ktx:19.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")

    implementation("com.google.android.gms:play-services-ads:19.3.0")
    implementation("com.google.android.ads.consent:consent-library:1.0.8")
    implementation("com.google.android.gms:play-services-auth:18.1.0")

    implementation("com.google.zxing:core:3.4.0")
    implementation("me.dm7.barcodescanner:zxing:1.9.13")

    implementation("com.google.android.material:material:1.2.0")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    implementation("io.arrow-kt:arrow-core:0.10.4")

    implementation("com.jakewharton.timber:timber:4.7.1")

    testImplementation("org.mockito:mockito-core:2.7.22")
}
