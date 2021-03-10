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
                "-P" +
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:navigation"))

    // Tools for Rulebook PDF parsing
    implementation("com.github.librepdf:openpdf:1.3.23")
    implementation("com.github.andob:android-awt:1.0.0")

    // Testing utilities
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("org.mockito:mockito-core:2.7.22")
}
