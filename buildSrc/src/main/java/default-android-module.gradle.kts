import com.android.build.gradle.LibraryExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

configure<LibraryExtension> {
    compileSdk = Versions.Android.compileSdk

    defaultConfig {
        minSdk = Versions.Android.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs +
            "-Xopt-in=androidx.compose.foundation.layout.ExperimentalLayout" +
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi" +
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi" +
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi" +
            "-P" +
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true" +
            "-Xallow-result-return-type"
    }
}

dependencies {
    // Testing utilities
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("org.mockito:mockito-core:2.7.22")
}