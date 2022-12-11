package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class RulebookLexer {

    fun getTokens(data: String): Sequence<Token> = sequence {
        var lastElement: Element? = null

        fixText(data).lineSequence().forEach { line ->
            for (element in Jsoup.parse(line).select("span, br")) {
                consumeSpan(element, lastElement)?.let { yield(it) }
                lastElement = element
            }
        }
    }

    sealed interface Token {
        class HeadingPart(val text: String) : Token

        sealed class ParagraphToken(val text: String, val type: String) : Token
        class SubHeadingPart(text: String) : ParagraphToken(text, "subHeading")
        class NormalPart(text: String) : ParagraphToken(text, "normal")
        class ItalicsPart(text: String) : ParagraphToken(text, "italics")
        object BlankLine : ParagraphToken(text = "\n\n", "blankLine")
        class BlockQuote(text: String) : ParagraphToken(text, "blockQuote")
        object LineBreak : ParagraphToken("\n", "lineBreak")

        sealed class TableValue(val text: String) : Token
        class BodyCellPart(text: String) : TableValue(text)
        class TableHeading(text: String) : TableValue(text)

        class BoxHeader(val text: String) : Token
    }

    fun fixText(text: String): String {
        return text.trim()
            .replace(Regex("T "), "T")
            .replace(Regex("V "), "V")
            .replace(Regex("Y "), "Y")
            .replace(Regex("W "), "W")
            .replace("  ", " ")
            .replace(", ,", ",")
    }

    private fun consumeSpan(element: Element, lastElement: Element?): Token? {
        val styles = element.attr("style")

        if (element.tagName() == "br") {
            return if (lastElement?.tagName() == "br")
                return Token.BlankLine
            else Token.LineBreak
        }

        factories.forEach { (style, factory) ->
            if (style in styles) {
                return factory(element.ownText())
            }
        }

        return null
    }

    companion object {
        // Note: Non-english locales may use commas instead of decimal points
        private val factories = listOf<Pair<Regex, (String) -> Token>>(
            Regex("height: 1[.,]57%") to Token::HeadingPart,
            Regex("height: 1[.,]31%") to Token::SubHeadingPart,
            Regex("height: 1[.,]21%") to Token::ItalicsPart,
            Regex("height: 1[.,]18%") to Token::NormalPart,
            Regex("height: 1[.,]11%") to Token::BodyCellPart,
            Regex("height: 1[.,]27%") to Token::TableHeading,
            Regex("height: 1[.,]16%") to Token::BoxHeader,
            Regex("height: 1[.,]01%") to Token::BoxHeader,
        )
    }
}
