package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import com.benasher44.uuid.uuid4
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object RulebookTraitImporter {

    private val factories = listOf<Pair<String, (String) -> Token>>(
        "height: 1.57%" to Token::HeadingPart,
        "height: 1.31%" to Token::SubHeadingPart,
        "height: 1.21%" to Token::ItalicsPart,
        "height: 1.18%" to Token::NormalPart,
    )

    fun importTraits(reader: PdfReader): Sequence<Trait> {
        val extractor = PdfTextExtractor(reader, true)

        return parseTraits(
            (338..343).joinToString("\n") { extractor.getTextFromPage(it) }
        )
    }

    private fun parseTraits(data: String): Sequence<Trait> = sequence {
        var heading = ""
        val description = mutableListOf<Token.ParagraphToken>()

        data.lineSequence().forEach { line ->
            for (span in Jsoup.parse(line).select("span")) {
                when (val token = consumeSpan(span)) {
                    is Token.ParagraphToken -> {
                        if (heading == "") {
                            // This is some text before the first heading
                            continue
                        }

                        description += token
                    }
                    is Token.HeadingPart -> {
                        if (description.isNotEmpty()) {
                            // This heading starts new section
                            yield(createTrait(heading, description))

                            heading = ""
                            description.clear()
                        }

                        heading += "${token.text} "
                    }
                    null -> { /* Skipped */ }
                }
            }
        }

        if (heading != "") {
            yield(createTrait(heading, description))
        }
    }

    private fun createTrait(name: String, description: List<Token.ParagraphToken>): Trait {
        val fixedName = fixText(name)

        return Trait(
            uuid4(),
            name = fixedName,
            description = fixText(buildMarkdown(description)),
            specifications = resolveSpecifications(fixedName),
        )
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

    sealed interface Token {
        class HeadingPart(val text: String) : Token

        sealed class ParagraphToken(val text: String, val type: String) : Token
        class SubHeadingPart(text: String) : ParagraphToken(text, "subHeading")
        class NormalPart(text: String) : ParagraphToken(text, "normal")
        class ItalicsPart(text: String) : ParagraphToken(text, "italics")
    }

    private fun buildMarkdown(tokens: List<Token.ParagraphToken>): String {
        val flattened = mutableListOf<NonEmptyList<Token.ParagraphToken>>()

        tokens.forEach { token ->
            val last = flattened.lastOrNull()

            if (last != null && last[0].type == token.type) {
                flattened[flattened.lastIndex] = last + token
            } else {
                flattened += nonEmptyListOf(token)
            }
        }

        return flattened.joinToString(" ") { tokenGroup ->
            val text: String = tokenGroup.joinToString(" ") { it.text }

            return@joinToString when (tokenGroup[0]) {
                is Token.NormalPart -> text
                is Token.ItalicsPart -> "*$text*"
                is Token.SubHeadingPart -> "\n\n#### ${text.trim()}\n"
            }
        }
    }

    private fun fixText(text: String): String {
        return text.trim()
            .replace(Regex("\n +"), "\n")
            .replace(Regex("^T "), "T")
            .replace(Regex("^V "), "\nV")
    }

    private fun consumeSpan(element: Element): Token? {
        val styles = element.attr("style")

        factories.forEach { (style, factory) ->
            if (style in styles) {
                return factory(element.ownText())
            }
        }

        return null
    }
}
