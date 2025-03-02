import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildkonfig)
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
}

multiplatformResources {
    resourcesPackage.set("cz.frantisekmasa.wfrp_master.common")
}

kotlin {
    androidTarget()
    jvm()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        all {
            @Suppress("OPT_IN_USAGE")
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            languageSettings.apply {
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.animation.ExperimentalAnimationApi")
                optIn("com.google.accompanist.permissions.ExperimentalPermissionsApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)

                api(libs.firebase.common)
                api(libs.firebase.firestore)
                api(libs.firebase.auth)
                api(libs.firebase.functions)

                implementation(libs.kotlinx.collections.immutable)

                implementation(libs.debounce)

                api(libs.voyager.navigator)
                api(libs.voyager.transitions)
                api(libs.voyager.screenmodel)

                api(libs.kodein.di.framework.compose)
                implementation(libs.kodein.di)

                api(libs.arrow)

                // Parser combinator library (grammars etc.)
                implementation(libs.better.parse)

                // Multiplatform UUID
                implementation(libs.uuid)

                // JSON encoding
                api(libs.kotlinx.serialization.json)

                // Logging
                api(libs.napier)

                // HTTP client
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.cio)
                api(libs.ktor.client.core)

                // Rich text
                implementation(libs.richtext.commonmark)
                implementation(libs.richtext.ui.material)
                implementation(libs.semver)

                api(libs.moko.parcelize)
                api(libs.moko.resources)
                api(libs.resources.compose)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.firebase.analytics)
                implementation(libs.firebase.crashlytics)

                // Permission management
                implementation(libs.accompanist.permissions)

                api(libs.appcompat)
                api(libs.activity.compose)

                implementation(libs.kodein.di.framework.android.core)

                // Authentication
                api(libs.play.services.auth)
                implementation(libs.firebase.dynamic.links.ktx)

                // Shared Preferences DataStore
                api(libs.datastore.preferences)

                implementation(libs.pdfbox.android)

                // Coil - image library
                implementation(libs.coil.compose)

                // Time picker
                implementation(libs.datetime.dialog)

                // QR codes
                implementation(libs.zxing.core)
                implementation(libs.camera.camera2)
                implementation(libs.camera.core)
                implementation(libs.camera.lifecycle)
                implementation(libs.camera.view)

                // Network availability check
                implementation(libs.reactivenetwork.rx2)
                implementation(libs.kotlinx.coroutines.rx2)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)

                implementation(libs.korau)
                implementation(libs.pdfbox)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
    }
}

android {
    namespace = "cz.frantisekmasa.wfrp_master.common"
    compileSdk = rootProject.extra["compile_sdk"].toString().toInt()

    defaultConfig {
        minSdk = rootProject.extra["min_sdk"].toString().toInt()
    }

    dependencies {
        // Allow use of Java 8 APIs on older Android versions
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }

    compileOptions {
        // Allow use of Java 8 APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        }
    }
}

buildkonfig {
    packageName = "cz.frantisekmasa.wfrp_master.common"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val properties =
            if (File("local.properties").exists()) {
                loadProperties("local.properties")
            } else {
                Properties()
            }

        buildConfigField(STRING, "functionsEmulatorUrl", properties.getOrDefault("dev.functionsEmulatorUrl", "").toString())
        buildConfigField(STRING, "firestoreEmulatorUrl", properties.getOrDefault("dev.firestoreEmulatorUrl", "").toString())
        buildConfigField(STRING, "versionName", System.getenv("SUPPLY_VERSION_NAME") ?: "dev")
        buildConfigField(BOOLEAN, "isDebugMode", properties.getOrDefault("dev.debugMode", "false").toString())
    }
}
