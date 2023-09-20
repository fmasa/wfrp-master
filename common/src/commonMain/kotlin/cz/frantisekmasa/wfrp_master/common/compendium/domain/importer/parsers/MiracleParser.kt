package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle

class MiracleParser {

    fun import(
        document: Document,
        structure: PdfStructure,
        pages: Sequence<Int>,
    ): List<Miracle> = buildList {
        val lexer = TwoColumnPdfLexer(document, structure)

        val tokens = pages
            .flatMap { lexer.getTokens(it).toList() }
            .flatten()
            .toList()

        val stream = TokenStream(tokens)

        stream.dropUntil { isCultHeading(it) }

        lateinit var cultName: String

        while (stream.peek() is Token.Heading3 || isCultHeading(stream.peek())) {
            if (isCultHeading(stream.peek())) {
                cultName = extractCultName(stream.consumeOneOfType<Token.Heading2>().text).trim()
            }

            val name = stream.consumeOneOfType<Token.Heading3>().text.trim()

            // Range: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val range = stream.consumeOneOfType<Token.NormalPart>().text

            // Target: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val target = stream.consumeOneOfType<Token.NormalPart>().text

            // Duration: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val (duration, effectStart) = stream.consumeOneOfType<Token.NormalPart>().text
                .split('\n', limit = 2)

            val effect = MarkdownBuilder.buildMarkdown(
                listOf(Token.NormalPart(effectStart)) +
                    stream.consumeUntil { it is Token.Heading3 || isCultHeading(it) }
                        .filterIsInstance<Token.ParagraphToken>()
            )

            add(
                Miracle(
                    id = uuid4(),
                    name = name,
                    range = range.trim(),
                    target = target.trim(),
                    duration = duration.trim(),
                    cultName = cultName,
                    effect = effect,
                )
            )
        }
    }

    private fun isCultHeading(token: Token?): Boolean {
        if (token !is Token.TextToken) {
            return false
        }

        val text = token.text.trim()

        return (token is Token.Heading2) && text.matches(cultHeadingRegex)
    }

    private fun extractCultName(text: String): String {
        return requireNotNull(cultHeadingRegex.matchEntire(text.trim())).groupValues[1]
            .lowercase()
            .replaceFirstChar { it.titlecase() }
    }

    companion object {
        private val cultHeadingRegex = Regex("Miracles of ([a-z]+)", RegexOption.IGNORE_CASE)
    }
}
