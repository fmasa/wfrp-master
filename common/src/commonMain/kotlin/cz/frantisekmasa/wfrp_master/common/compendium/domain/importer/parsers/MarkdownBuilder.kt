package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

object MarkdownBuilder {

    private const val bulletPointPlaceholder = "<bullet-point>"

    fun buildMarkdown(tokens: List<Token.ParagraphToken>): String {
        val flattened = mutableListOf<NonEmptyList<Token.ParagraphToken>>()

        tokens.forEach { token ->
            val last = flattened.lastOrNull()

            if (last != null && (last[0].type == token.type || token is Token.LineBreak)) {
                flattened[flattened.lastIndex] = last + token
            } else {
                flattened += nonEmptyListOf(token)
            }
        }

        var lastToken: Token.ParagraphToken? = null

        val lines = flattened.joinToString(" ") { tokenGroup ->
            val text: String = tokenGroup.joinToString(" ") { it.text }

            val result = when (tokenGroup[0]) {
                is Token.NormalPart -> {
                    if (lastToken is Token.BlockQuote) {
                        val lines = text.lines()
                        val quoteAuthor = lines[0]

                        buildString {
                            append("\n**")
                            append(quoteAuthor)
                            append("**\n\n")
                            append(lines.drop(1).joinToString("\n"))
                        }
                    } else text
                }
                is Token.ItalicsPart -> "*${text.trim()}*"
                is Token.BoldPart -> "**${text.trim()}**"
                is Token.Heading3 -> "\n\n#### ${text.trim()}\n"
                is Token.BlankLine -> "\n\n"
                is Token.BlockQuote -> "\n\n" + text.lines().joinToString("\n") { "> $it" }
                is Token.LineBreak, Token.BlankLine -> "\n\n"
                is Token.BulletPoint -> bulletPointPlaceholder
            }

            lastToken = tokenGroup[0]

            result
        }.trim()
            .lines()
            .map { it.trim() }
            .toList()

        val lastBulletPointLineIndex = lines.indexOfLast { it.startsWith(bulletPointPlaceholder) }

        return buildString {
            var wasBulletPoint = false

            for (index in lines.indices) {
                val line = lines[index]

                if (line.startsWith(bulletPointPlaceholder)) {
                    appendLine(
                        line.replaceRange(
                            0,
                            bulletPointPlaceholder.length,
                            "- ",
                        )
                    )
                    wasBulletPoint = true
                    continue
                }

                if (wasBulletPoint && index > lastBulletPointLineIndex) {
                    appendLine() // There must be empty line between list and rest of the content
                    wasBulletPoint = false
                }

                appendLine(line)
            }
        }.replace("  ", " ")
    }
}
