package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.MarkdownBuilder
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TokenStream
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TwoColumnPdfLexer

/**
 * Parses trapping descriptions from this format (markdown for readability):
 *
 * ## Trapping category
 * **Trapping one:** Description of Trapping one
 * **Trapping two:** Description of Trapping two
 */
class ListDescriptionParser : TrappingDescriptionParser {

    override fun parse(
        document: Document,
        structure: PdfStructure,
        pages: IntRange,
    ): List<Pair<String, String>> {
        val lexer = TwoColumnPdfLexer(document, structure)
        val stream = TokenStream(
            pages
                .asSequence()
                .map { lexer.getTokens(it) }
                .flatMap { sequenceOf(it.first, it.second) }
                .flatten()
                .filter { it !is Token.TableValue }
                .toList()
        )

        val descriptionsByName = mutableMapOf<String, String>()
        stream.dropUntil(::isName)

        while (stream.peek() != null) {
            val name = stream.consumeOneOfType<Token.BoldPart>()
                .text.trim { it == ':' || it.isWhitespace() }

            val description = MarkdownBuilder.buildMarkdown(
                stream.consumeUntil { isName(it) || it is Token.Heading }
                    .filterIsInstance<Token.ParagraphToken>()
            ).replace(NEWLINES_TO_REMOVE_REGEX, " ")

            descriptionsByName[comparableName(name)] = description

            if (name.contains(" and ", ignoreCase = true)) {
                name.splitToSequence(" and ", ignoreCase = true)
                    .forEach { descriptionsByName[comparableName(it)] = description }
            }
            val nextToken = stream.peek()

            if (nextToken != null && !isName(nextToken)) {
                stream.dropUntil(::isName)
            }
        }

        return descriptionsByName.toList()
    }

    private fun isName(token: Token): Boolean {
        return token is Token.BoldPart &&
            (token.text.endsWith(':') || token.text.endsWith(": ")) &&
            !token.text.startsWith("Example:")
    }

    companion object {
        private val NEWLINES_TO_REMOVE_REGEX = Regex("\\n(?!(\\*\\*_?Example:))")
    }
}
