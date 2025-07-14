package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken as ParseToken

sealed interface Token {
    val text: String get() = ""

    data class Metadata(
        val y: Float,
        val height: Float,
    ) {
        companion object {
            val Empty = Metadata(0f, 0f)
        }
    }

    sealed class TextToken(override val text: String, val metadata: Metadata) : Token

    interface Heading : Token {
        override val text: String
    }

    class Heading1(text: String, metadata: Metadata) : TextToken(text, metadata), Heading {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    class Heading2(text: String, metadata: Metadata) : TextToken(text, metadata), Heading {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    class Heading3(text: String, metadata: Metadata) : ParagraphToken(text, "heading3", metadata), Heading {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    sealed class ParagraphToken(text: String, val type: String, metadata: Metadata) : TextToken(text, metadata) {
        override fun toString(): String {
            return "ParagraphToken(type=$type, text=$text, metadata=$metadata)"
        }
    }

    class NormalPart(text: String, metadata: Metadata) : ParagraphToken(text, "normal", metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    class ItalicsPart(text: String, metadata: Metadata) : ParagraphToken(text, "italics", metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    class BoldPart(text: String, metadata: Metadata) : ParagraphToken(text, "bold", metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    class BoldItalicPart(text: String, metadata: Metadata) : ParagraphToken(text, "bold-italic", metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
    }

    object BlankLine : ParagraphToken(text = "\n\n", "blankLine", Metadata(0f, 0f))

    class BlockQuote(text: String, metadata: Metadata) : ParagraphToken(text, "blockQuote", metadata)

    object LineBreak : ParagraphToken("\n", "lineBreak", Metadata(0f, 0f))

    sealed class TableValue(override val text: String, val metadata: Metadata) : Token

    class BodyCellPart(
        text: String,
        metadata: Metadata,
    ) : TableValue(text, metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)

        override fun toString(): String {
            return "BodyCellPart(text=$text, metadata=$metadata)"
        }
    }

    class TableHeadCell(text: String, metadata: Metadata) : TableValue(text, metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
        override fun toString(): String {
            return "TableHeadCell(text=$text, metadata=$metadata)"
        }
    }

    class TableHeading(text: String, metadata: Metadata) : TableValue(text, metadata) {
        constructor(token: ParseToken) : this(token.text, token.metadata)
        override fun toString(): String {
            return "TableHeading(text=$text, metadata=$metadata)"
        }
    }

    object BulletPoint : ParagraphToken("\n -", "bulletPoint", Metadata(0f, 0f))

    object CrossIcon : Token

    class BoxHeader(override val text: String, val metadata: Metadata) : Token {
        constructor(token: ParseToken) : this(token.text, token.metadata)

        override fun toString(): String {
            return "BoxHeader(text=$text, metadata=$metadata)"
        }
    }

    class BoxContent(override val text: String, val metadata: Metadata) : Token {
        constructor(token: ParseToken) : this(token.text, token.metadata)

        override fun toString(): String {
            return "BoxContent(text=$text, metadata=$metadata)"
        }

    }
}
