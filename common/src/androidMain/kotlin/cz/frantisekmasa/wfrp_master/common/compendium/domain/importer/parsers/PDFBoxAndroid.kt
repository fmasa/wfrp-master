package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import android.graphics.Path
import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

actual typealias TextPosition = com.tom_roush.pdfbox.text.TextPosition
actual typealias Document = PDDocument
actual typealias Font = PDFont
actual typealias Page = PDPage

actual abstract class PdfTextStripper : PDFTextStripper() {
    protected actual val textCharactersByArticle: List<List<TextPosition>> get() = charactersByArticle

    protected actual abstract fun onPageEnter()

    protected actual abstract fun onTextLine(
        text: String,
        textPositions: List<TextPosition>,
    )

    override fun writePage() {
        onPageEnter()
        super.writePage()
    }

    protected actual abstract fun onFinish()

    override fun writeString(
        text: String,
        textPositions: List<TextPosition>,
    ) {
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

actual abstract class RectangleFinder actual constructor(page: Page) :
    PDFGraphicsStreamEngine(page) {
        actual abstract fun appendRectangle(
            points: List<Pair<Double, Double>>,
            components: List<Float>,
        )

        override fun appendRectangle(
            p0: PointF,
            p1: PointF,
            p2: PointF,
            p3: PointF,
        ) {
            appendRectangle(
                listOf(p0, p1, p2, p3).map { it.x.toDouble() to it.y.toDouble() },
                graphicsState.nonStrokingColor.components.toList(),
            )
        }

        override fun drawImage(pdImage: PDImage?) {
        }

        override fun clip(windingRule: Path.FillType?) {
        }

        override fun moveTo(
            x: Float,
            y: Float,
        ) {
        }

        override fun lineTo(
            x: Float,
            y: Float,
        ) {
        }

        override fun curveTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            x3: Float,
            y3: Float,
        ) {
        }

        override fun getCurrentPoint(): PointF {
            return PointF(0f, 0f)
        }

        override fun closePath() {
        }

        override fun endPath() {
        }

        override fun strokePath() {
        }

        override fun fillPath(windingRule: Path.FillType?) {
        }

        override fun fillAndStrokePath(windingRule: Path.FillType?) {
        }

        override fun shadingFill(shadingName: COSName?) {
        }
    }
