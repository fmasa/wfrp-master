package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.MarkdownBuilder
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TokenStream
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TwoColumnPdfLexer

inline fun <reified T : Enum<T>> matchEnumOrNull(
    value: String,
    aliases: Map<String, T> = emptyMap(),
): T? {
    if (value in aliases) {
        return aliases.getValue(value)
    }

    val comparableValue = value
        .replace('-', '_')
        .replace(' ', '_')

    return enumValues<T>().firstOrNull { feature ->
        feature.name.equals(comparableValue, ignoreCase = true)
    }
}

inline fun <reified T : Enum<T>> matchEnumSetOrNull(value: String, separator: String): Set<T>? {
    val items = value.split(separator, ignoreCase = true)
    val enums = items.mapNotNull { matchEnumOrNull<T>(it.trim()) }

    if (enums.size < items.size) {
        return null
    }

    return enums.toSet()
}

val FEATURE_REGEX = Regex("([a-zA-Z- ])+ ?\\+?([0-9])?")
private val NAME_WITH_COUNT_PATTERN = Regex("(.*) \\((\\d+|dozen)\\)")

inline fun <reified T : Enum<T>> parseFeatures(value: String): Map<T, Int> {
    if (value == "–") {
        return emptyMap()
    }

    return value.splitToSequence(",")
        .map { it.trim() }
        .mapNotNull {
            val (name, rating) = FEATURE_REGEX.matchEntire(it)?.groupValues
                ?: error("Invalid feature $it")
            val feature = matchEnumOrNull<T>(name) ?: return@mapNotNull null

            feature to (rating.toIntOrNull() ?: 0)
        }.toMap()
}

fun parseNameAndPackSize(value: String): Pair<String, Int> {
    val result = NAME_WITH_COUNT_PATTERN.matchEntire(value)
        ?: return value to 1

    return Pair(
        result.groupValues[1],
        if (result.groupValues[2] == "dozen")
            12
        else result.groupValues[2].toInt(),
    )
}

fun descriptionsByName(
    document: Document,
    structure: PdfStructure,
    pages: IntRange,
): Map<String, String> {
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
    val isName: (Token) -> Boolean = {
        it is Token.BoldPart && (it.text.endsWith(':') || it.text.endsWith(": "))
    }

    stream.dropUntil(isName)

    while (stream.peek() != null) {
        val name = stream.consumeOneOfType<Token.BoldPart>()
            .text.trim { it == ':' || it.isWhitespace() }

        val description = MarkdownBuilder.buildMarkdown(
            stream.consumeUntil { isName(it) || it is Token.Heading }
                .filterIsInstance<Token.ParagraphToken>()
        )

        descriptionsByName[name] = description.replace('\n', ' ')

        val nextToken = stream.peek()

        if (nextToken != null && !isName(nextToken)) {
            stream.dropUntil(isName)
        }
    }

    return descriptionsByName
}
