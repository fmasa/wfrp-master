
import com.lowagie.text.pdf.PdfReader
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookCareerImporter
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ImporterTest {
    @Test
    @Ignore("Intended for development only")
    fun test() {
        val rulebook = javaClass.getResourceAsStream("rulebook.pdf")
        PdfReader(rulebook).use { reader ->
            assertEquals(64, RulebookCareerImporter().importCareers(reader).count())
        }
    }
}
