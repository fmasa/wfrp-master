package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

/**
 * PDF lexer that determines text position from order in which it's laid in PDF structure.
 * When working with two-page layout, [TwoColumnPdfLexer] should be used instead as that will
 * work even for PDFs that lay text in nonsensical order.
 */
class DefaultLayoutPdfLexer(
    private val document: Document,
    private val structure: PdfStructure,
    private val mergeSubsequentTokens: Boolean = true,
) {

    fun getTokens(page: Int): Sequence<Token> {
        val stripper = TextStripper()
        stripper.setStartPage(page)
        stripper.setEndPage(page)
        stripper.writeText(document, NullWriter())

        return stripper.tokens
            .asSequence()
            .mapNotNull(structure::resolveToken)
    }

    private inner class TextStripper : PdfTextStripper() {
        val currentText: StringBuilder = StringBuilder()
        var lastTextPosition: TextPosition? = null
        val tokens = mutableListOf<TextToken>()

        override fun onPageEnter() {
        }

        private fun buildToken() {
            val text = currentText.toString()
            currentText.clear()
            val position = lastTextPosition

            if (text.isBlank() || position == null) {
                return
            }

            tokens += createToken(text, position)
        }

        override fun onTextLine(text: String, textPositions: List<TextPosition>) {
            var lastPositionInLine: TextPosition? = null

            for (position in textPositions) {
                val lastPosition = lastTextPosition

                if (
                    lastPosition == null ||
                    (
                        !structure.areSameStyle(
                            lastPosition,
                            position
                        ) && position.getUnicode() != " "
                        )
                ) {
                    buildToken()
                    lastTextPosition = position
                }

                if (
                    !mergeSubsequentTokens &&
                    lastPositionInLine != null &&
                    lastPositionInLine.getUnicode() == " " &&
                    position.getX() - lastPositionInLine.getEndX() > position.getWidth() * 5
                ) {
                    // Sometimes text is laid as single line even when it should be separate.
                    // This happens mostly in tables, so we want to split parts tokens with long
                    // space between them.
                    currentText.deleteCharAt(currentText.lastIndex)
                    buildToken()
                    lastTextPosition = position
                }

                currentText.append(position.getUnicode())

                lastPositionInLine = position
            }

            if (mergeSubsequentTokens) {
                currentText.append("\n")
            } else {
                buildToken()
                lastTextPosition = null
            }
        }

        override fun onFinish() {
            buildToken()
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
