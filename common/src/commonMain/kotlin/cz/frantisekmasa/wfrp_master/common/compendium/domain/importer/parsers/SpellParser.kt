package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell

class SpellParser(
    private val isEnd: (Token?) -> Boolean = { it == null },
) {

    fun import(
        document: Document,
        structure: PdfStructure,
        pageRanges: Sequence<IntRange>,
    ): List<Spell> {
        val lexer = TwoColumnPdfLexer(document, structure)

        return pageRanges
            .flatMap { parseFromPages(lexer, it) }
            .toList()
    }

    private fun parseFromPages(lexer: TwoColumnPdfLexer, pages: IntRange): Sequence<Spell> = sequence {
        val tokens = pages.asSequence()
            .flatMap { lexer.getTokens(it).toList() }
            .flatten()
            .toList()

        val stream = TokenStream(tokens)

        stream.dropUntil { isLoreHeading(it) }

        while (!isEnd(stream.peek())) {
            val loreHeading = stream.consumeOneOfType<Token.TextToken>().text.trim()
            val lore = extractLore(loreHeading)

            yieldAll(consumeSpells(lore, stream))
            stream.dropUntil { isLoreHeading(it) || isEnd(it) }
        }
    }

    private fun consumeSpells(lore: String, stream: TokenStream): Sequence<Spell> = sequence {
        stream.dropUntil { it is Token.Heading3 }

        while (stream.peek() is Token.Heading3) {
            val name = stream.consumeOneOfType<Token.Heading3>().text.trim()

            // CN: <Int>
            stream.consumeOneOfType<Token.BoldPart>()
            val castingNumber = stream.consumeOneOfType<Token.NormalPart>().text.trim().toInt()

            // Range: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val range = consumeAtLeastOneLine(stream)

            // Target: <String>
            stream.consumeOneOfType<Token.BoldPart>()

            lateinit var duration: String
            lateinit var effectStart: String
            lateinit var target: String

            val targetTokenText = consumeAtLeastOneLine(stream)

            if (stream.peek() is Token.BoldPart) {
                target = targetTokenText
                stream.consumeOneOfType<Token.BoldPart>() // Duration:

                val lines = consumeAtLeastOneLine(stream).split('\n', limit = 2)

                duration = lines[0]
                effectStart = lines[1]
            } else {
                // See "Purple Pall of Shyish" - which has wrong formatting
                val lines = targetTokenText.split('\n', limit = 3)

                target = lines[0].trim()
                duration = lines[1].replace("Duration:", "").trim()
                effectStart = lines[2]
            }

            yield(
                Spell(
                    id = uuid4(),
                    name = name,
                    range = range.trim(),
                    target = target.trim(),
                    duration = duration.trim(),
                    castingNumber = castingNumber,
                    effect = MarkdownBuilder.buildMarkdown(
                        listOf(
                            listOf(Token.NormalPart(effectStart)),
                            stream.consumeUntil { it is Token.Heading },
                        ).filterIsInstance<Token.ParagraphToken>()
                    ),
                    lore = lore,
                )
            )
        }
    }

    private fun consumeAtLeastOneLine(stream: TokenStream): String {
        var text = stream.consumeOneOfType<Token.NormalPart>().text

        if ('\n' !in text && stream.peek() is Token.ItalicsPart) {
            // Line can contain `or`
            text += stream.consumeOneOfType<Token.ItalicsPart>().text
            text += stream.consumeOneOfType<Token.NormalPart>().text
        }

        return text
    }

    private fun isLoreHeading(token: Token): Boolean {
        if (token !is Token.TextToken) {
            return false
        }

        val text = token.text.trim()

        return (token is Token.Heading1 || token is Token.Heading2) &&
            (specialLores.keys.any { it.equals(text, ignoreCase = true) } || text.matches(loreHeadingRegex))
    }

    private fun extractLore(text: String): String {
        val specialLore = specialLores.entries.firstOrNull { it.key.equals(text, ignoreCase = true) }
            ?.value

        if (specialLore != null) {
            return specialLore
        }

        return requireNotNull(loreHeadingRegex.matchEntire(text)).groupValues[2]
            .lowercase()
            .replaceFirstChar { it.titlecase() }
    }

    companion object {
        private val loreHeadingRegex = Regex("(The )?Lore of ([a-z]+)", RegexOption.IGNORE_CASE)
        private val specialLores = mapOf(
            "Petty Spells" to "Petty Spells",
            "Arcane Spells" to "Arcane Spells",
            "New Arcane Spells" to "Arcane Spells",
        )
    }
}
