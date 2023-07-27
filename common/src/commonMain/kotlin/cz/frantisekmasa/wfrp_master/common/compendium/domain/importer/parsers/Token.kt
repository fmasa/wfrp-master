package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

sealed interface Token {

    sealed class TextToken(val text: String) : Token

    interface Heading : Token {
        val text: String
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
    object BlankLine : ParagraphToken(text = "\n\n", "blankLine")
    class BlockQuote(text: String) : ParagraphToken(text, "blockQuote")
    object LineBreak : ParagraphToken("\n", "lineBreak")

    class OptionsBoxHeading(text: String) : Token

    sealed class TableValue(val text: String) : Token
    class BodyCellPart(text: String) : TableValue(text)
    class TableHeadCell(text: String) : TableValue(text) {
        override fun toString(): String {
            return "TableHeadCell(text=$text)"
        }
    }
    class TableHeading(text: String) : TableValue(text)
    object BulletPoint : ParagraphToken("\n -", "bulletPoint")
    object CrossIcon : Token

    class BoxHeader(val text: String) : Token
}
