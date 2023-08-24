package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import androidx.compose.runtime.Composable
import java.io.Closeable
import java.io.InputStream
import java.io.Writer

expect class TextPosition {
    fun getFontSizeInPt(): Float
    fun getX(): Float
    fun getEndX(): Float
    fun getWidth(): Float
    fun getHeight(): Float
    fun getFont(): Font
    fun getUnicode(): String
}

expect class Document : Closeable {
    fun getPage(index: Int): Page
}

expect class Page

expect abstract class Font {
    abstract fun getName(): String
}

expect abstract class PdfTextStripper constructor() {
    fun setStartPage(page: Int)
    fun setEndPage(page: Int)
    fun setSortByPosition(enabled: Boolean)

    protected val textCharactersByArticle: List<List<TextPosition>>
    protected abstract fun onTextLine(text: String, textPositions: List<TextPosition>)

    fun writeText(document: Document, writer: Writer)
    protected abstract fun onPageEnter()
    protected abstract fun onFinish()
}

expect fun loadDocument(inputStream: InputStream): Document

@Composable
expect fun pdfBoxInitializer(): () -> Unit

expect abstract class RectangleFinder constructor(page: Page) {
    abstract fun appendRectangle(
        points: List<Pair<Double, Double>>,
        components: List<Float>,
    )

    fun processPage(page: Page)
}

data class Point(
    val x: Float,
    val y: Float,
)
