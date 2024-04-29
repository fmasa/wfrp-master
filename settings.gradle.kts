pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.hq.hydraulic.software")
    }
}

rootProject.name = "rpg"
include(":common")
include(":app")
include(":desktop")
