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
    implementation(project(":app:core"))

    // TODO: Wrap by adapter so that this does not leak to rest of the app
    // Then change this from api() to implementation()
    api("com.github.zsoltk:compose-router:0.23.0")
}