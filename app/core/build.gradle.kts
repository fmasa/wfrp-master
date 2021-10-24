import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("default-android-module")
    kotlin("plugin.serialization")
}

android {
    defaultConfig {
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
}

dependencies {
    // Basic Kotlin stuff
    api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    // Basic Android stuff 
    api("androidx.core:core-ktx:1.5.0")
    api("androidx.fragment:fragment-ktx:1.3.5")

    // Jetpack Compose
    api("androidx.compose.ui:ui:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling:${Versions.compose}")
    api("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    api("androidx.activity:activity-compose:1.3.0-beta02")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")

    // Koin
    api("io.insert-koin:koin-android:3.1.2")

    // Coil - image library
    implementation("io.coil-kt:coil-compose:1.3.2")

    // Firebase-related dependencies
    api(platform("com.google.firebase:firebase-bom:28.4.2"))
    api("com.google.firebase:firebase-analytics-ktx")
    api("com.google.firebase:firebase-auth-ktx")
    api("com.google.firebase:firebase-firestore-ktx")
    api("com.google.firebase:firebase-crashlytics")
    api("com.google.firebase:firebase-dynamic-links-ktx")
    api("com.google.firebase:firebase-functions-ktx")

    // Logging
    api("com.jakewharton.timber:timber:4.7.1")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    api("io.arrow-kt:arrow-core:0.10.4")

    // Parser combinator library (grammars etc.)
    api("com.github.h0tk3y.betterParse:better-parse:0.4.2")

    // JSON encoding
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // Premium
    // TODO: Make implementation only
    api("com.revenuecat.purchases:purchases:4.0.2")

    // Ads
    api("com.google.android.gms:play-services-ads:20.4.0")

    // Shared Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha05")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")

    // HTTP Client
    val ktorVersion = "1.6.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    api("com.google.accompanist:accompanist-flowlayout:0.12.0")
}
