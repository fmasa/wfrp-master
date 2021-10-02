plugins {
    id("default-android-module")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:navigation"))

    // Tools for Rulebook PDF parsing
    implementation("com.github.librepdf:openpdf:1.3.25")
    implementation("com.github.andob:android-awt:1.0.0")
}
