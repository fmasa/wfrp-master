package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

sealed interface Token {

    val text: String get() = ""

    sealed class TextToken(override val text: String) : Token

    interface Heading : Token {
        override val text: String
    }

    class Heading1(text: String) : TextToken(text), Heading
    class Heading2(text: String) : TextToken(text), Heading
    class Heading3(text: String) : ParagraphToken(text, "heading3"), Heading

    sealed class ParagraphToken(text: String, val type: String) : TextToken(text) {
        override fun toString(): String {
            return "ParagraphToken(type=$type, text=$text)"
        }
    }
    class NormalPart(text: String) : ParagraphToken(text, "normal")
    class ItalicsPart(text: String) : ParagraphToken(text, "italics")
    class BoldPart(text: String) : ParagraphToken(text, "bold")
    class BoldItalicPart(text: String) : ParagraphToken(text, "bold-italic")
    object BlankLine : ParagraphToken(text = "\n\n", "blankLine")
    class BlockQuote(text: String) : ParagraphToken(text, "blockQuote")
    object LineBreak : ParagraphToken("\n", "lineBreak")

    sealed class TableValue(override val text: String) : Token
    class BodyCellPart(
        text: String,
        val y: Float,
        val height: Float,
    ) : TableValue(text) {
        override fun toString(): String {
            return "BodyCellPart(text=$text, x=$y, height=$height)"
        }
    }
    class TableHeadCell(text: String) : TableValue(text) {
        override fun toString(): String {
            return "TableHeadCell(text=$text)"
        }
    }
    class TableHeading(text: String) : TableValue(text)
    object BulletPoint : ParagraphToken("\n -", "bulletPoint")
    object CrossIcon : Token

    class BoxHeader(override val text: String) : Token
}
