import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.ArchivesOfTheEmpire1
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.EnemyInShadowsCompanion
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.SeaOfClaws
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

object ConfigProvider {
    fun getRulebookPdf(book: Book): File? {
        val pdfFileName = bookFileName(book) ?: return null
        val config = getConfig() ?: return null

        return File(config.rulebooksPath, pdfFileName)
    }

    fun getPreviousResultsPath(): String? {
        return getConfig()?.previousResultsPath
    }

    fun getOutputPath(): String? {
        return getConfig()?.outputPath
    }

    private fun bookFileName(book: Book) =
        when (book) {
            CoreRulebook -> "rulebook.pdf"
            WindsOfMagic -> "winds_of_magic.pdf"
            UpInArms -> "up_in_arms.pdf"
            EnemyInShadowsCompanion -> "enemy_in_shadows_companion.pdf"
            ArchivesOfTheEmpire1 -> "archives_of_the_empire_1.pdf"
            SeaOfClaws -> "sea_of_claws.pdf"
            else -> null
        }

    private fun getConfig(): Config? {
        val configContent =
            javaClass.getResourceAsStream("config.json")?.use {
                it.bufferedReader().readText()
            } ?: return null

        return Json.decodeFromString<Config>(configContent)
    }
}

@Serializable
data class Config(
    val previousResultsPath: String,
    val outputPath: String,
    val rulebooksPath: String,
)
