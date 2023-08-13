import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

object Versions {
    private const val versionsFile = "versions.properties"

    val compose = loadPropertyFromResources(versionsFile, "composeVersion")
    val kotlin = loadPropertyFromResources(versionsFile, "kotlinVersion")
    val ktor = "2.0.0"

    val napier = loadPropertyFromResources(versionsFile, "napierVersion")

    object Android {
        val minSdk = loadPropertyFromResources(versionsFile, "androidMinSdk").toInt()
        val compileSdk = loadPropertyFromResources(versionsFile, "androidCompileSdk").toInt()
        val targetSdk = loadPropertyFromResources(versionsFile, "androidTargetSdk").toInt()
    }
}
