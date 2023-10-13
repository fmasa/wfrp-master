package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.careers.CareerCharacteristicsParser

class SkillParser {

    fun import(
        document: Document,
        structure: PdfStructure,
        pages: Sequence<Int>,
    ): List<Skill> = buildList {
        val lexer = TwoColumnPdfLexer(document, structure)
        val tokens = pages
            .asSequence()
            .flatMap { page -> lexer.getTokens(page).toList() }
            .flatten()

        val stream = TokenStream(tokens.toList())

        stream.dropWhile { it !is Token.Heading3 }

        while (stream.peek() != null) {
            val name = stream.consumeOneOfType<Token.Heading3>().text.trim()
            val characteristic = CareerCharacteristicsParser.CHARACTERISTICS.getValue(
                stream.consumeOneOfType<Token.BoldPart>().text.trim('(', ')', ' ')
                    .lowercase()
            )

            val skillType = stream.consumeOneOfType<Token.BoldPart>().text.trim(' ', ')', '\n')

            val advanced = when (skillType.splitToSequence(",").first()) {
                "advanced" -> true
                "basic" -> false
                else -> error("Expected basic/advanced as skill type, $skillType found")
            }
            val (description, specialisations) = splitSpecialisationsFromDescription(
                stream.consumeUntil { it is Token.Heading3 || it is Token.BoxHeader }
            )

            // Drop the Options box tokens if necessary
            stream.dropUntil { it is Token.Heading3 }

            specialisations.forEach {
                add(
                    Skill(
                        id = uuid4(),
                        name = "$name$it",
                        description = MarkdownBuilder.buildMarkdown(
                            description.filterIsInstance<Token.ParagraphToken>()
                        ),
                        characteristic = characteristic,
                        advanced = advanced,
                    )
                )
            }
        }
    }

    private fun splitSpecialisationsFromDescription(
        description: List<Token>,
    ): Pair<List<Token>, Sequence<String>> {
        val specialisationsTokenIndex = description.indexOfFirst {
            it is Token.BoldPart && it.text.startsWith("Specialisations:")
        }

        if (specialisationsTokenIndex == -1) {
            return description to sequenceOf("")
        }

        val specialisationsList = description[specialisationsTokenIndex + 1] as Token.ParagraphToken

        return description to specialisationsList.text
            .splitToSequence(',')
            .map { " (${it.trim()})" }
    }
}
