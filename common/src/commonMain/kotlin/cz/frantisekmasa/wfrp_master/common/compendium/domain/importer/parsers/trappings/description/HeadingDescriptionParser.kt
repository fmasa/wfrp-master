package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TokenStream
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TwoColumnPdfLexer

class HeadingDescriptionParser : TrappingDescriptionParser {
    override fun parse(
        document: Document,
        structure: PdfStructure,
        pages: IntRange,
    ): List<Pair<String, String>> {
        val lexer = TwoColumnPdfLexer(document, structure)
        val stream =
            TokenStream(
                pages
                    .asSequence()
                    .map { lexer.getTokens(it) }
                    .flatMap { sequenceOf(it.first, it.second) }
                    .flatten()
                    .toList(),
            )

        return buildList {
            while (stream.peek() != null) {
                val heading = stream.consumeOneOfType<Token.Heading3>().text
                val text = stream.consumeOneOfType<Token.NormalPart>().text.trim()

                for (name in names(heading)) {
                    add(name to text.trim())
                }
            }
        }
    }

    private fun names(heading: String): List<String> {
        val comparableName = comparableName(heading)

        return buildList {
            add(comparableName)

            val matches = AND_REGEX.matchEntire(comparableName) ?: return@buildList

            add("${matches.groupValues[1]} ${matches.groupValues[2]}")
            add("${matches.groupValues[1]} ${matches.groupValues[3]}")
        }
    }

    companion object {
        private val AND_REGEX = Regex("(.*) (\\w+) & (\\w+)")
    }
}
