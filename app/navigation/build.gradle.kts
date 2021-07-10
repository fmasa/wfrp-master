plugins {
    id("default-android-module")
}

dependencies {
    implementation(project(":app:core"))

    // Navigation
    api("androidx.navigation:navigation-compose:2.4.0-alpha03")
}
