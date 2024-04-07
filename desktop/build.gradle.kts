
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
    id("dev.hydraulic.conveyor") version "1.4"
}

configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        named("jvmMain") {
            languageSettings.apply {
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
            }
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                implementation("org.slf4j:slf4j-simple:1.7.36")
            }
        }
    }
}

dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

compose.desktop {
    application {
        mainClass = "cz.frantisekmasa.wfrp_master.desktop.WfrpMasterApplication"
        version = System.getenv("APP_VERSION") ?: "1.0.0"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "wfrp-master"
            packageVersion = System.getenv("APP_VERSION") ?: "1.0.0"

            windows {
                menuGroup = "Compose Examples"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "BF9CDA6A-1391-46D5-9ED5-383D6E68CCEB"
            }

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            }
        }
    }
}
