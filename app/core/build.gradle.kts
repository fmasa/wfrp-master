plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

val composeVersion = "1.0.0-alpha09"

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(21)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = freeCompilerArgs +
                "-Xallow-jvm-ir-dependencies" +
                "-Xopt-in=androidx.compose.foundation.layout.ExperimentalLayout" +
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi" +
                "-P" +
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    // Basic Kotlin stuff
    api("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
    api("org.jetbrains.kotlin:kotlin-stdlib:1.4.21")

    // Basic Android stuff 
    api("androidx.core:core-ktx:1.3.2")
    api("androidx.fragment:fragment-ktx:1.3.0-rc01")

    // Styles
    api("com.google.android.material:material:1.2.1")

    // Jetpack Compose
    api("androidx.compose.ui:ui:$composeVersion")
    api("androidx.compose.material:material:$composeVersion")
    api("androidx.compose.ui:ui-tooling:$composeVersion")

    // Koin
    api("org.koin:koin-android:2.2.0")
    api("org.koin:koin-androidx-viewmodel:2.2.0")
    api("org.koin:koin-androidx-fragment:2.2.0")

    // Firebase-related dependencies
    api("com.google.firebase:firebase-analytics:18.0.0")
    api("com.firebaseui:firebase-ui-auth:6.2.0")
    api("com.google.firebase:firebase-firestore-ktx:22.0.1")
    api("com.google.firebase:firebase-analytics-ktx:18.0.0")
    api("com.google.firebase:firebase-crashlytics:17.3.0")
    api("com.google.firebase:firebase-dynamic-links-ktx:19.1.1")

    // Logging
    api("com.jakewharton.timber:timber:4.7.1")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")

    api("io.arrow-kt:arrow-core:0.10.4")

    // JSON encoding
    // TODO: Make is implementation only
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")

    // Testing utilities
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("org.mockito:mockito-core:2.7.22")
}