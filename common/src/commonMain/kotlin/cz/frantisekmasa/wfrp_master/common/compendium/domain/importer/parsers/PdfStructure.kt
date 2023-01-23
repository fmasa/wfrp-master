package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

interface PdfStructure {

    fun areSameStyle(a: TextPosition, b: TextPosition): Boolean {
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
