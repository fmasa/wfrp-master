package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import androidx.compose.runtime.Composable
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.graphics.image.PDImage
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.geom.Point2D
import java.io.InputStream
import org.apache.pdfbox.text.TextPosition as PdfboxTextPosition

actual typealias TextPosition = PdfboxTextPosition
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
    return { } // JVM library does not need initialization
}

actual abstract class RectangleFinder actual constructor(page: Page) :
    PDFGraphicsStreamEngine(page) {
        actual abstract fun appendRectangle(
            points: List<Pair<Double, Double>>,
            components: List<Float>,
        )

        override fun appendRectangle(
            p0: Point2D,
            p1: Point2D,
            p2: Point2D,
            p3: Point2D,
        ) {
            appendRectangle(
                listOf(p0, p1, p2, p3).map { it.x to it.y },
                graphicsState.nonStrokingColor.components.toList(),
            )
        }

        override fun drawImage(pdImage: PDImage?) {
        }

        override fun clip(windingRule: Int) {
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

        override fun getCurrentPoint(): Point2D {
            return Point2D.Float(0f, 0f)
        }

        override fun closePath() {
        }

        override fun endPath() {
        }

        override fun strokePath() {
        }

        override fun fillPath(windingRule: Int) {
        }

        override fun fillAndStrokePath(windingRule: Int) {
        }

        override fun shadingFill(shadingName: COSName?) {
        }
    }
