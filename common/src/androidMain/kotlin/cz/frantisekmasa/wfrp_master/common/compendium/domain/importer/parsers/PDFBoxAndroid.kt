package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

actual typealias TextPosition = com.tom_roush.pdfbox.text.TextPosition
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
    val context = LocalContext.current

    return {
        if (!PDFBoxResourceLoader.isReady()) {
            PDFBoxResourceLoader.init(context)
        }
    }
}
