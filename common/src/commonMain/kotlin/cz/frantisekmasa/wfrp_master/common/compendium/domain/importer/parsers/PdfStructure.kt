package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

interface PdfStructure {
    /**
     * Are PDF tokens sorted by their position on page?
     */
    val tokensSorted: Boolean get() = true

    /**
     * Are table footnotes normal text tokens or do they reuse table cell tokens?
     */
    val tableFootnotesAsNormalText get() = false

    fun areSameStyle(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        return a.getFont().getName() == b.getFont().getName() &&
            a.getFontSizeInPt() == b.getFontSizeInPt() &&
            a.getHeight() == b.getHeight()
    }

    /**
     * Returns token when text with given should be considered part of page content
     * or NULL when it's heading, page number, etc...
     */
    fun resolveToken(textToken: TextToken): Token?
}
