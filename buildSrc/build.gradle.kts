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
val kotlinVersion = versions["kotlinVersion"]

dependencies {
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
