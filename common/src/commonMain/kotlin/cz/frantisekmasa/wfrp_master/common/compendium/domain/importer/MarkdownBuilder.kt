package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookLexer.Token

object MarkdownBuilder {

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

        return flattened.joinToString(" ") { tokenGroup ->
            val text: String = tokenGroup.joinToString(" ") { it.text }

            val result = when (tokenGroup[0]) {
                is Token.NormalPart, Token.BlankLine -> {
                    if (lastToken is Token.BlockQuote)
                        "\n$text\n\n"
                    else text
                }
                is Token.ItalicsPart -> "*$text*"
                is Token.SubHeadingPart -> "\n\n#### ${text.trim()}\n"
                is Token.BlankLine -> "\n\n"
                is Token.BlockQuote -> "\n\n" + text.lines().joinToString("\n") { "> $it" } + "\n\n"
                is Token.LineBreak -> "\n"
            }

            lastToken = tokenGroup[0]

            result
        }.trim()
    }
}
