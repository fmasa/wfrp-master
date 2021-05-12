plugins {
    id("default-android-module")
}

dependencies {
    implementation(project(":app:core"))

    // Navigation
    api("androidx.navigation:navigation-compose:1.0.0-alpha09")
}