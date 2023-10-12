package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait

class TraitParser {

    fun import(
        document: Document,
        structure: PdfStructure,
        pages: Sequence<Int>,
    ): List<Trait> = buildList {
        val lexer = TwoColumnPdfLexer(document, structure)
        val tokens = pages
            .flatMap { lexer.getTokens(it).toList() }
            .flatten()
            .toList()

        val stream = TokenStream(tokens)

        stream.dropUntil { it is Token.Heading3 }

        while (stream.peek() != null) {
            val name = stream.consumeOneOfType<Token.Heading3>().text.trim()
            val specifications = resolveSpecifications(name)

            val description = MarkdownBuilder.buildMarkdown(
                stream
                    .consumeUntil { it is Token.Heading3 || it is Token.BoxHeader }
                    .filterIsInstance<Token.ParagraphToken>()
            )

            // Drop the Options box tokens if necessary
            stream.dropUntil { it is Token.Heading3 }

            add(
                Trait(
                    id = uuid4(),
                    name = name,
                    specifications = specifications,
                    description = description,
                )
            )
        }
    }

    private fun resolveSpecifications(name: String): Set<String> = sequence {
        listOf("#", "Rating").forEach {
            if (it in name) {
                yield(it)
            }
        }

        // Trailing value in braces
        Regex("\\((.*)\\)").find(name)?.let { yield(it.groupValues[1]) }
    }.toSet()
}
