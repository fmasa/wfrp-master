import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.PdfCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.ArchivesOfTheEmpire1
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.EnemyInShadowsCompanion
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.loadDocument
import cz.frantisekmasa.wfrp_master.common.compendium.import.BlessingImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.CareerImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.MiracleImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SkillImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SpellImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TalentImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TraitImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TrappingImport
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.Assume.assumeTrue
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

class CompendiumImportRegressionTest {
    @Test
    fun `Core Rulebook`() {
        assertMatchesPreviousRuns(CoreRulebook)
    }

    @Test
    fun `Winds of Magic`() {
        assertMatchesPreviousRuns(WindsOfMagic)
    }

    @Test
    fun `Up in Arms`() {
        assertMatchesPreviousRuns(UpInArms)
    }

    @Test
    fun `Enemy in Shadows - Companion`() {
        assertMatchesPreviousRuns(EnemyInShadowsCompanion)
    }

    @Test
    fun `Archives of The Empire I`() {
        assertMatchesPreviousRuns(ArchivesOfTheEmpire1)
    }

    private fun assertMatchesPreviousRuns(book: Book) {
        val pdf = ConfigProvider.getRulebookPdf(book)
        assumeTrue("PDF is found", pdf != null && pdf.exists())
        val document = loadDocument(pdf!!.inputStream())
        val importer = PdfCompendiumImporter(document, book)

        val runner = Runner(bookName = pdf.nameWithoutExtension)

        with(runner) {
            runBlocking {
                import("skills", importer.importSkills().map(SkillImport::fromSkill))
                import("talents", importer.importTalents().map(TalentImport::fromTalent))
                import("spells", importer.importSpells().map(SpellImport::fromSpell))
                import("blessings", importer.importBlessings().map(BlessingImport::fromBlessing))
                import("miracles", importer.importMiracles().map(MiracleImport::fromMiracle))
                import("careers", importer.importCareers().map(CareerImport::fromCareer))
                import("traits", importer.importTraits().map(TraitImport::fromTrait))
                import("trappings", importer.importTrappings().map(TrappingImport::fromTrapping))
            }
        }

        assertEquals(emptyList(), runner.failedCompendiums)
    }

    class Runner(
        private val bookName: String,
    ) {
        val failedCompendiums = mutableListOf<String>()

        inline fun <reified T> import(
            name: String,
            items: List<T>,
        ) {
            import(name, items, serializer(), serializer())
        }

        fun <T> import(
            name: String,
            items: List<T>,
            serializationStrategy: SerializationStrategy<List<T>>,
            deserializationStrategy: DeserializationStrategy<List<T>>,
        ) {
            val previousResults =
                File("${ConfigProvider.getPreviousResultsPath()}/$bookName/$name.json")
            println("Loading previous $name compendium import result from ${previousResults.absolutePath}")
            val previousItems =
                previousResults.takeIf { it.exists() }?.let {
                    json.decodeFromString(
                        deserializationStrategy,
                        it.readText(),
                    )
                } ?: emptyList()

            if (items != previousItems) {
                if (items.isNotEmpty()) {
                    val outputPath =
                        Path(ConfigProvider.getOutputPath() ?: error("Output path not set"))
                    val file =
                        outputPath.also { if (!it.exists()) it.createDirectory() }
                            .resolve(bookName).also { if (!it.exists()) it.createDirectory() }
                            .resolve("$name.json")

                    println("Writing $name compendium import result to ${file.absolutePathString()}")
                    val result = json.encodeToString(serializationStrategy, items)
                    file.writeText(result)
                } else {
                    println("No $name compendium import result")
                }
                failedCompendiums.add(name)
            }
        }
    }

    companion object {
        private val json =
            Json {
                encodeDefaults = true
                prettyPrint = true
            }
    }
}
