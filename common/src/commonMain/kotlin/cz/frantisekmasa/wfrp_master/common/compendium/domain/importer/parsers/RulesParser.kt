package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import java.util.Locale

class RulesParser(
    private val excludedBoxes: Set<String> = emptySet(),
    private val excludedTables: Set<String> = emptySet(),
) {
    fun import(
        stream: TokenStream,
        headings: List<(Token) -> Boolean> = HEADING_STRUCTURE,
    ): Sequence<JournalEntry> =
        sequence {
            stream.dropUntil(headings[0])

            yieldAll(parseSection(stream, parents = emptyList(), headings))
        }

    private fun parseSection(
        stream: TokenStream,
        parents: List<String>,
        headings: List<(Token) -> Boolean>,
    ): Sequence<JournalEntry> =
        sequence {
            val depth = parents.size
            val isCurrentLevelHeading = headings[depth]
            val isChildHeading = headings.getOrNull(depth + 1) ?: { false }

            while (stream.peek() != null) {
                if (!isCurrentLevelHeading(stream.peek()!!) && !isChildHeading(stream.peek()!!)) {
                    return@sequence
                }

                val heading = stream.consumeOne()
                assert(isCurrentLevelHeading(heading)) {
                    "Expected heading of level ${parents.size + 1}, $heading (${heading.text}) given"
                }
                val headingText = cleanupHeading(heading.text)

                val content = stream.consumeUntil { token -> headings.any { it(token) } }

                if (content.isNotEmpty()) {
                    yield(
                        JournalEntry(
                            id = uuid4(),
                            name = headingText,
                            text =
                                MarkdownBuilder.buildMarkdown(
                                    cleanupDescription(content).filterIsInstance<Token.ParagraphToken>(),
                                ),
                            gmText = "",
                            isPinned = false,
                            parents = parents,
                            isVisibleToPlayers = true,
                        ),
                    )
                }

                if (stream.peek()?.let(isChildHeading) == true) {
                    yieldAll(parseSection(stream, parents + headingText, headings))
                }
            }
        }

    private fun cleanupDescription(description: List<Token>): List<Token> {
        val stream = TokenStream(description)

        return buildList {
            while (stream.peek() != null) {
                val token = stream.consumeOne()

                if (
                    token is Token.BoxHeader &&
                    excludedTables.any { it.equals(token.text.trim(), ignoreCase = true) }
                ) {
                    break
                }

                if (
                    token is Token.BoxHeader &&
                    excludedBoxes.any { it.equals(token.text.trim(), ignoreCase = true) }
                ) {
                    stream.dropWhile { it is Token.BoxContent }
                    continue
                }

                if (token is Token.BoxHeader) {
                    // Make sure we do not merge two boxes
                    add(Token.BlankLine)
                    add(Token.BlockQuote("### ${cleanupHeading(token.text)}"))
                    continue
                }

                if (token is Token.BoxContent) {
                    add(Token.BlockQuote(token.text))
                    continue
                }

                add(token)
            }
        }
    }

    private fun cleanupHeading(heading: String): String {
        return heading
            .replace("\t", " ")
            .trim()
            .split(' ')
            .joinToString(" ") { word ->
                if (NOT_CAPITALIZED_WORDS.any { it.equals(word, ignoreCase = true) }) {
                    return@joinToString word.lowercase()
                }

                word.lowercase()
                    .replaceFirstChar { it.titlecase(Locale.getDefault()) }
            }
    }

    companion object {
        private val NOT_CAPITALIZED_WORDS = setOf("and", "of")
        val HEADING_STRUCTURE =
            listOf<(Token) -> Boolean>(
                { it is Token.Heading1 },
                { it is Token.Heading2 },
                { it is Token.Heading3 },
            )
    }
}
