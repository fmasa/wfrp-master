import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("default-android-module")
}

android {
    defaultConfig {
        //
        // Firestore emulator setup
        //
        val propertiesFile = File("local.properties")

        val properties = if (propertiesFile.exists())
            loadProperties("local.properties")
        else Properties()

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
    api("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
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
    api("org.koin:koin-android:2.2.0")
    api("org.koin:koin-androidx-viewmodel:2.2.0")

    // Coil - image library
    implementation("io.coil-kt:coil-compose:1.3.2")

    // Firebase-related dependencies
    api("com.google.firebase:firebase-analytics:19.0.0")
    api("com.firebaseui:firebase-ui-auth:6.2.0")
    api("com.google.firebase:firebase-firestore-ktx:23.0.1")
    api("com.google.firebase:firebase-analytics-ktx:19.0.0")
    api("com.google.firebase:firebase-crashlytics:18.1.0")
    api("com.google.firebase:firebase-dynamic-links-ktx:20.1.0")

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
    // TODO: Make is implementation only
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

    // Premium
    // TODO: Make implementation only
    api("com.revenuecat.purchases:purchases:4.0.2")

    // Ads
    api("com.google.android.gms:play-services-ads:20.2.0")

    // Shared Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha05")

    // HTTP Client
    val ktorVersion = "1.6.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")

    api("com.google.accompanist:accompanist-flowlayout:0.12.0")
}
