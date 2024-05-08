package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.careers.CareerCharacteristicsParser
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus

class CareerParser(
    private val convertTablesToText: Boolean = false,
) {
    fun import(
        document: Document,
        structure: PdfStructure,
        classPageRanges: Sequence<Pair<SocialClass, Iterable<Int>>>,
    ): List<Career> {
        val lexer = TwoColumnPdfLexer(document, structure)
        val characteristicsParser = CareerCharacteristicsParser(document, structure)

        return classPageRanges
            .flatMap { (socialClass, pages) ->
                pages.asSequence()
                    .map { page ->
                        val columns = lexer.getTokens(page)

                        val career =
                            parseCareerPage(
                                socialClass,
                                columns.first,
                                columns.second,
                            )

                        val characteristics =
                            characteristicsParser.findCharacteristics(page)
                                .groupBy({ it.level - 1 }, { it.characteristic })

                        career.copy(
                            levels =
                                career.levels.mapIndexed { index, level ->
                                    level.copy(
                                        characteristics =
                                            characteristics[index]?.toSet()
                                                ?: emptySet(),
                                    )
                                },
                        )
                    }
            }.toList()
    }

    private fun parseCareerPage(
        socialClass: SocialClass,
        firstColumn: Sequence<Token>,
        secondColumn: Sequence<Token>,
    ): Career {
        val stream =
            TokenStream(
                if (convertTablesToText) {
                    firstColumn
                        .map {
                            when (it) {
                                is Token.BodyCellPart -> Token.NormalPart(it.text)
                                is Token.TableHeadCell -> Token.BoldPart(it.text)
                                else -> it
                            }
                        }.toList()
                } else {
                    firstColumn.toList()
                },
            )

        val name =
            stream.consumeOneOfType<Token.Heading>().text
                .trim()
                .splitToSequence(' ', '\n')
                .map { it.lowercase() }
                .map { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
                .joinToString(" ")

        val text =
            stream.consumeOneOfType<Token.NormalPart>().text
                .lineSequence()
                .filter { it.isNotBlank() }
                .toList()

        val descriptionStart = mutableListOf<Token.ParagraphToken>()

        if (text.size > 1) {
            descriptionStart += text.drop(1).map { Token.NormalPart(it) }
        }

        val species =
            text[0]
                .splitToSequence(",")
                .map { it.trim() }
                .map { value ->
                    Race.values().first {
                        value.contains(it.name.replace("_", " "), ignoreCase = true)
                    }
                }
                .toSet()

        if (descriptionStart.isEmpty()) {
            descriptionStart += stream.consumeOneOfType<Token.ItalicsPart>()
            descriptionStart += Token.BlankLine
            descriptionStart += stream.consumeWhileOfType<Token.ParagraphToken>()
        }

        val description =
            descriptionStart +
                secondColumn.filterIsInstance<Token.ParagraphToken>().map {
                    if (it is Token.ItalicsPart) {
                        Token.BlockQuote(it.text)
                    } else {
                        it
                    }
                }

        val isAttributesSection: (Token) -> Boolean = {
            it is Token.BoxHeader ||
                it is Token.Heading2 ||
                it is Token.TableHeading ||
                it is Token.TableHeadCell
        }
        stream.dropUntil(isAttributesSection)
        stream.dropWhile(isAttributesSection)

        val levels =
            (0 until 4).map {
                val (levelName, status) =
                    stream.consumeOneOfType<Token.BoldPart>().text
                        .split("\n")
                        .first()
                        .let { fixLevelLine(it) }
                        .splitToSequence(levelLineDelimiterRegex)
                        .map { it.trim() }
                        .toList()

                stream.dropWhile { it is Token.BoldPart }

                val skills =
                    stream.consumeUntil { it is Token.BoldPart }
                        .filterIsInstance<Token.ParagraphToken>()

                stream.dropWhile { it is Token.BoldPart }

                val talents =
                    buildText(
                        stream.consumeUntil { it is Token.BoldPart }
                            .filterIsInstance<Token.ParagraphToken>(),
                    )

                stream.dropWhile { it is Token.BoldPart }

                val trappings =
                    buildText(
                        stream.consumeUntil { it is Token.BoldPart }
                            .filterIsInstance<Token.ParagraphToken>(),
                    )

                val (tier, standing) =
                    status
                        .splitToSequence(" ")
                        .map { it.trim() }
                        .toList()

                CareerLevel(
                    name = levelName.trim(),
                    status =
                        SocialStatus(
                            SocialStatus.Tier.values().first { it.name.equals(tier, ignoreCase = true) },
                            standing.toInt(),
                        ),
                    skills = skills,
                    talents = talents,
                    trappings = trappings,
                )
            }

        stream.assertEnd()

        val incomeSkill = parseIncomeSkill(levels[0].skills)

        return Career(
            id = uuid4(),
            name = name,
            description = MarkdownBuilder.buildMarkdown(description),
            socialClass = socialClass,
            races = species,
            levels =
                levels.map { level ->
                    Career.Level(
                        id = uuid4(),
                        name = level.name,
                        status = level.status,
                        characteristics = emptySet(),
                        skills =
                            buildText(level.skills)
                                .splitToSequence(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                                .map { Career.Skill(it, it == incomeSkill) }
                                .toList(),
                        talents =
                            level.talents.splitToSequence(",")
                                .map { it.trim() }
                                .toList(),
                        trappings =
                            level.trappings.splitToSequence(",")
                                .map { it.trim() }
                                .toList(),
                    )
                },
        )
    }

    private fun fixLevelLine(line: String): String {
        return line
            .let {
                if (levelLineDelimiters.none { it in line }) {
                    val spaces = line.indices.filter { line[it] == ' ' }
                    val penultimateSpaceIndex = spaces[spaces.size - 2]

                    return@let line.replaceRange(
                        penultimateSpaceIndex..penultimateSpaceIndex,
                        levelLineDelimiters[0].toString(),
                    )
                }

                line
            }
            .let { if (it.startsWith("night")) "K$it" else it } // Fix Knight import from UiA
    }

    private fun buildText(tokens: List<Token.ParagraphToken>): String {
        return tokens.joinToString(" ") { it.text }
            .trimStart(':') // Fix leading : from Talents: and such
            .trimEnd() // Fix trailing "K" from Knight line in UiA
            .replace("( ", "(")
            .replace(") ", ")")
            .replace(Regex("[ \\n]+"), " ")
    }

    private fun parseIncomeSkill(skills: List<Token.ParagraphToken>): String? {
        return skills.indices.filter { skills[it] is Token.ItalicsPart }
            .filter {
                // Such as "Melee (Basic *or* Fencing)"
                skills[it].text.trim() != "or" &&
                    // Specialisations may use italics
                    (it == 0 || !skills[it - 1].text.endsWith("("))
            }.firstNotNullOfOrNull { skills[it].text }
    }

    private data class CareerLevel(
        val name: String,
        val status: SocialStatus,
        val skills: List<Token.ParagraphToken>,
        val talents: String,
        val trappings: String,
    )

    companion object {
        private val levelLineDelimiters = listOf('–', '—')
        private val levelLineDelimiterRegex = Regex("[${levelLineDelimiters.joinToString("|")}]")
    }
}
