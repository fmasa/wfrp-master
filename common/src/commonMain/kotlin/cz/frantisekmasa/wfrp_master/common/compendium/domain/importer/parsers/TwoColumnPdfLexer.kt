package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import java.io.Writer
import kotlin.properties.Delegates

/**
 * PDF lexer that determines text position from their coordinates and interprets the pages
 * as two columns.
 */
class TwoColumnPdfLexer(
    private val document: Document,
    private val structure: PdfStructure,
) {

    fun getTokens(page: Int): Pair<Sequence<Token>, Sequence<Token>> {
        val stripper = TextStripper()
        stripper.setStartPage(page)
        stripper.setEndPage(page)
        stripper.writeText(document, Writer.nullWriter())

        return Pair(
            stripper.columns[0].tokens
                .asSequence()
                .mapNotNull(structure::resolveToken),
            stripper.columns[1].tokens
                .asSequence()
                .mapNotNull(structure::resolveToken)
        )
    }

    private inner class Column {
        val currentText: StringBuilder = StringBuilder()
        var lastTextPosition: TextPosition? = null
        val tokens = mutableListOf<TextToken>()

        fun buildToken() {
            val text = currentText.toString()
            currentText.clear()
            val position = lastTextPosition

            if (text.isBlank() || position == null) {
                return
            }

            tokens += createToken(text, position)
        }
    }

    private inner class TextStripper : PdfTextStripper() {
        private var pageCenter by Delegates.notNull<Float>()
        val columns = listOf(Column(), Column())

        init {
            setSortByPosition(true)
        }

        override fun onPageEnter() {
            val characters = textCharactersByArticle
                .asSequence()
                .flatten()
                .filter { structure.resolveToken(createToken("", it)) != null }

            val minX = characters.minOf { it.getX() }
            val maxX = characters.maxOf { it.getEndX() }

            pageCenter = (minX + maxX) / 2
        }

        override fun onTextLine(text: String, textPositions: List<TextPosition>) {
            val touchedColumns = mutableSetOf<Column>()

            for (position in textPositions) {
                val column = if (position.getX() <= pageCenter)
                    columns[0]
                else columns[1]

                touchedColumns += column
                val lastPosition = column.lastTextPosition

                if (
                    lastPosition == null ||
                    (!structure.areSameStyle(lastPosition, position) && position.getUnicode() != " ")
                ) {
                    column.buildToken()
                    column.lastTextPosition = position
                }

                column.currentText.append(position.getUnicode())
            }
            touchedColumns.forEach { it.currentText.append("\n") }
        }

        override fun onFinish() {
            columns.forEach { it.buildToken() }
        }
    }

    private fun createToken(text: String, position: TextPosition): TextToken {
        return TextToken(
            text,
            position.getFont().getName(),
            position.getHeight(),
            position.getFontSizeInPt(),
        )
    }
}

