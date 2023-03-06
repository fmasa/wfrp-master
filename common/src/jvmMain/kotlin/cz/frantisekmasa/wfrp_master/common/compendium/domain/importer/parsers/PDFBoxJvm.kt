package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import androidx.compose.runtime.Composable
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream
import org.apache.pdfbox.text.TextPosition as PdfboxTextPosition

actual typealias TextPosition = PdfboxTextPosition
actual typealias Document = PDDocument
actual typealias Font = PDFont

actual abstract class PdfTextStripper : PDFTextStripper() {
    protected actual val textCharactersByArticle: List<List<TextPosition>> get() = charactersByArticle

    protected actual abstract fun onPageEnter()

    protected actual abstract fun onTextLine(text: String, textPositions: List<TextPosition>)

    override fun writePage() {
        onPageEnter()
        super.writePage()
    }

    protected actual abstract fun onFinish()

    override fun writeString(text: String, textPositions: List<TextPosition>) {
        onTextLine(text, textPositions)
    }

    override fun endDocument(document: PDDocument) {
        onFinish()
    }
}

actual fun loadDocument(inputStream: InputStream): Document {
    return PDDocument.load(
        inputStream,
        MemoryUsageSetting.setupTempFileOnly(),
    )
}

@Composable
actual fun pdfBoxInitializer(): () -> Unit {
    return { } // JVM library does not need initialization
}
