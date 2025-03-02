import org.jlleitschuh.gradle.ktlint.KtlintExtension

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    configure<KtlintExtension> {
        filter {
            exclude { it.file.path.contains("build/generated") }
            exclude { it.file.path.contains("build/buildkonfig") }
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.moko.resources) apply false
}
