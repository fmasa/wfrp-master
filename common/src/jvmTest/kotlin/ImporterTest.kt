
import com.lowagie.text.pdf.PdfReader
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookCareerImporter
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookCompendiumImporter
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore("Intended for development only")
class ImporterTest {

    @Test
    fun `careers import`() {
        withRulebook { reader ->
            assertEquals(64, RulebookCareerImporter().importCareers(reader).count())
        }
    }

    @Test
    fun `talents import`() {
        withRulebook { reader ->
            val talents = runBlocking { RulebookCompendiumImporter(reader).importTalents() }

            assertEquals(167, talents.size)
        }
    }

    private fun withRulebook(block: (PdfReader) -> Unit) {
        val rulebook = javaClass.getResourceAsStream("rulebook.pdf")
        PdfReader(rulebook).use(block)
    }
}
