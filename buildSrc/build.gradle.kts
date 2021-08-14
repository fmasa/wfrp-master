import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

val versions = loadProperties("$projectDir/src/main/resources/versions.properties")
val agpVersion = "7.1.0-alpha04"
val kotlinVersion = versions["kotlinVersion"]

dependencies {
    implementation("com.android.tools.build:gradle:$agpVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
