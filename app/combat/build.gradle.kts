plugins {
    id("default-android-module")
    kotlin("plugin.serialization")
}

android {
    buildTypes {
        release {
            resValue("string", "combat_ad_unit_id", "ca-app-pub-8647604386686373/3858132571")
        }

        debug {
            resValue("string", "combat_ad_unit_id", "ca-app-pub-3940256099942544/6300978111")
        }
    }
}

dependencies {
    implementation(project(":app:core"))
    implementation(project(":app:navigation"))
    implementation(project(mapOf("path" to ":app:inventory")))
}
