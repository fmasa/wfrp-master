import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

object Versions {
    private const val versionsFile = "versions.properties"

    val agp = loadPropertyFromResources(versionsFile, "androidGradlePluginVersion")
    val compose = loadPropertyFromResources(versionsFile, "composeVersion")
    val kotlin = loadPropertyFromResources(versionsFile, "kotlinVersion")
}
