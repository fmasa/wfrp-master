plugins {
    id("default-android-module")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:compendium"))
    implementation(project(":app:navigation"))
}
