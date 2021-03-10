plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

val composeVersion = "1.0.0-beta02"

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
                "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi" +
                "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi" +
                "-P" +
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    // Basic Kotlin stuff
    api("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
    api("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")

    // Basic Android stuff 
    api("androidx.core:core-ktx:1.3.2")
    api("androidx.fragment:fragment-ktx:1.3.0")

    // Jetpack Compose
    api("androidx.compose.ui:ui:$composeVersion")
    api("androidx.compose.material:material:$composeVersion")
    api("androidx.compose.ui:ui-tooling:$composeVersion")
    api("androidx.compose.runtime:runtime-livedata:$composeVersion")
    api("androidx.activity:activity-compose:1.3.0-alpha04")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha03")


    // Koin
    api("org.koin:koin-android:2.2.0")
    api("org.koin:koin-androidx-viewmodel:2.2.0")

    // Firebase-related dependencies
    api("com.google.firebase:firebase-analytics:18.0.2")
    api("com.firebaseui:firebase-ui-auth:6.2.0")
    api("com.google.firebase:firebase-firestore-ktx:22.1.0")
    api("com.google.firebase:firebase-analytics-ktx:18.0.2")
    api("com.google.firebase:firebase-crashlytics:17.3.1")
    api("com.google.firebase:firebase-dynamic-links-ktx:19.1.1")

    // Logging
    api("com.jakewharton.timber:timber:4.7.1")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")

    api("io.arrow-kt:arrow-core:0.10.4")

    // Parser combinator library (grammars etc.)
    api("com.github.h0tk3y.betterParse:better-parse:0.4.0")

    // JSON encoding
    // TODO: Make is implementation only
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")

    // Premium
    // TODO: Make implementation only
    api("com.revenuecat.purchases:purchases:4.0.2")

    // Ads
    api("com.google.android.gms:play-services-ads:19.7.0")

    // Shared Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha05")

    // HTTP Client
    val ktorVersion = "1.5.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")

    // Testing utilities
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("org.mockito:mockito-core:2.7.22")
}