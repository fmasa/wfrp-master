plugins {
    id("default-android-module")
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:compendium"))
    implementation(project(":app:navigation"))
}
