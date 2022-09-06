
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common:firebase"))
                implementation(project(":common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.0")
                implementation("org.slf4j:slf4j-simple:1.7.36")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "cz.frantisekmasa.wfrp_master.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "WFRP Master"
            packageVersion = "1.0.0"

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
