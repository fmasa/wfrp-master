package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import com.benasher44.uuid.uuid4
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus

class RulebookCareerImporter {

    private enum class ParserState {
        RACES,
        SUMMARY,
        BOX_HEADER,
        CAREER_LEVELS,
    }

    fun importCareers(reader: PdfReader): Sequence<Career> {
        val extractor = PdfTextExtractor(reader, true)
        val lexer = RulebookLexer()

        return classPageRanges
            .asSequence()
            .flatMap { (socialClass, pages) ->
                pages.asSequence()
                    .mapNotNull { page ->
                        val tokens = lexer.getTokens(extractor.getTextFromPage(page)).toList()

                        val career = parseCareerPage(socialClass, lexer, tokens.asSequence())

                        if (career == null) {
                            println(page)
                        }

                        career
                    }
            }
    }

    private fun parseCareerPage(
        socialClass: SocialClass,
        lexer: RulebookLexer,
        tokens: Sequence<RulebookLexer.Token>,
    ): Career? {
        var state = ParserState.RACES
        var races = ""
        var incomeSkill = ""

        val summary = mutableListOf<RulebookLexer.Token.ParagraphToken>()
        val currentCareerLevelBlock = StringBuilder()
        val previousCareerLevels = mutableListOf<String>()

        for (token in tokens) {
            if (state == ParserState.RACES) {
                when (token) {
                    is RulebookLexer.Token.NormalPart -> {
                        races += " " + token.text
                    }
                    is RulebookLexer.Token.ItalicsPart -> {
                        state = ParserState.SUMMARY
                    }
                    is RulebookLexer.Token.LineBreak -> {}
                    else -> {
                        return null
                    }
                }
            }

            if (state == ParserState.SUMMARY) {
                when (token) {
                    is RulebookLexer.Token.ParagraphToken -> {
                        summary += token
                    }
                    is RulebookLexer.Token.BoxHeader -> {
                        state = ParserState.BOX_HEADER
                    }
                    is RulebookLexer.Token.LineBreak -> {}
                    else -> return null
                }
            }

            if (state == ParserState.BOX_HEADER) {
                when (token) {
                    is RulebookLexer.Token.NormalPart -> {
                        state = ParserState.CAREER_LEVELS
                    }
                    is RulebookLexer.Token.BoxHeader, RulebookLexer.Token.BlankLine -> {}
                    is RulebookLexer.Token.LineBreak -> {}
                    else -> return null
                }
            }

            if (state == ParserState.CAREER_LEVELS) {
                when (token) {
                    is RulebookLexer.Token.NormalPart -> {
                        currentCareerLevelBlock
                            .append(" ")
                            .append(token.text)

                        if (
                            incomeSkill.isNotEmpty() &&
                            incomeSkill.substring(incomeSkill.length - 1) !in setOf(",", ")", ":")
                        ) {
                            if (token.text.replace(" ", "").startsWith("Talents")) {
                                incomeSkill += ","
                            } else {
                                incomeSkill += " " + token.text
                            }
                        }
                    }
                    is RulebookLexer.Token.LineBreak -> {
                        currentCareerLevelBlock.append('\n')
                    }
                    is RulebookLexer.Token.BlankLine -> {
                        previousCareerLevels += currentCareerLevelBlock.toString()
                        currentCareerLevelBlock.clear()
                    }
                    is RulebookLexer.Token.ItalicsPart -> {
                        // Only first Career Level contains Skill marked in italics, this must
                        // be rest of the summary.
                        if (previousCareerLevels.isNotEmpty() && token.text != "or") {
                            if (currentCareerLevelBlock.isNotEmpty()) {
                                previousCareerLevels += currentCareerLevelBlock.toString()
                            }

                            summary += token
                            state = ParserState.SUMMARY
                            continue
                        }

                        if (token.text != "or") {
                            incomeSkill += " " + token.text
                        }

                        currentCareerLevelBlock
                            .append(" ")
                            .append(token.text)
                    }
                    else -> return null
                }
            }
        }

        val careerLevels = previousCareerLevels
            .asSequence()
            .filter { it.isNotBlank() }
            .flatMap { splitCareerLevelsIfNecessary(it) }
            .map { lexer.fixText(it.replace("\n", " ")) }
            .map { CareerLevelGrammar.parseToEnd(it) }
            .map {
                Career.Level(
                    id = uuid4(),
                    name = it.name,
                    status = it.status,
                    characteristics = emptySet(),
                    skills = resolveIncomeSkills(incomeSkill, it.skills),
                    talents = it.talents,
                    trappings = it.trappings,
                )
            }
            .toList()

        if (careerLevels.size != 4) {
            return null
        }

        return Career(
            id = uuid4(),
            name = careerLevels[1].name,
            levels = careerLevels,
            description = lexer.fixText(
                MarkdownBuilder.buildMarkdown(
                    summary.map {
                        if (it is RulebookLexer.Token.ItalicsPart)
                            RulebookLexer.Token.BlockQuote(it.text)
                        else it
                    }
                )
            ),
            socialClass = socialClass,
            races = races.splitToSequence(",")
                .map { it.trim() }
                .map { it.replace(" ", "_") }
                .map { value -> Race.values().first { it.name.equals(value, ignoreCase = true) } }
                .toSet()
        )
    }

    private fun resolveIncomeSkills(
        incomeSkillText: String,
        skills: List<String>,
    ): List<Career.Skill> {
        return skills.map {
            Career.Skill(
                expression = it,
                isIncomeSkill = it in incomeSkillText,
            )
        }
    }

    private fun splitCareerLevelsIfNecessary(careerLevelBlock: String): Sequence<String> {
        val lines = careerLevelBlock.lines()

        val firstLineOfNextLevel = lines.indexOfLast { "—" in it }

        if (firstLineOfNextLevel == 0) {
            return sequenceOf(careerLevelBlock)
        }

        return sequenceOf(
            lines.slice(0 until firstLineOfNextLevel).joinToString("\n"),
            lines.slice(firstLineOfNextLevel..lines.lastIndex).joinToString("\n"),
        )
    }

    private object CareerLevelGrammar : Grammar<CareerLevel>() {
        private val tier by regexToken(
            Regex(
                "(" +
                    SocialStatus.Tier.values().joinToString("|") { it.name } +
                    ")(?= \\d)",
                RegexOption.IGNORE_CASE,
            )
        )

        private val trappingsKey by literalToken("Trappings: ")
        private val talentsKey by literalToken("Talents: ")
        private val skillsKey by literalToken("Skills: ")
        private val word by regexToken("[0-9]*[a-zA-Z'()/’‘!-]+[0-9]*")
        private val digit by regexToken("[0-9]")
        private val comma by regexToken("\\s?,\\s?")
        private val space by literalToken(" ")
        private val dash by literalToken("—")

        override val rootParser: Parser<CareerLevel> =
            (
                separatedTerms(
                    word,
                    space
                ) map { words -> words.joinToString(" ") { it.text } }
                ) * // Career level name
                skip(optional(space)) * skip(dash) * skip(optional(space)) *
                tier *
                skip(space) *
                digit * // standing
                skip(zeroOrMore(space)) *
                skip(skillsKey) * separatedTerms(
                // Skills
                separatedTerms(word, space) map { words ->
                    words.joinToString(" ") { it.text }
                },
                comma,
            ) *
                skip(zeroOrMore(space)) *
                skip(talentsKey) * separatedTerms(
                // Talents
                separatedTerms(word, space) map { words ->
                    words.joinToString(" ") { it.text }
                },
                comma,
            ) *
                skip(zeroOrMore(space)) *
                skip(trappingsKey) * separatedTerms(
                // Trappings
                separatedTerms(
                    word map { it.text } or (oneOrMore(digit) map { digits -> digits.joinToString { it.text } }),
                    space,
                ) map { it.joinToString(" ") },
                comma,
            ) map { (name, tier, standing, skills, talents, trappings) ->
                CareerLevel(
                    name = name,
                    status = SocialStatus(
                        SocialStatus.Tier.values()
                            .first { it.name.equals(tier.text, ignoreCase = true) },
                        standing.text.toInt(),
                    ),
                    skills = skills.map { it.trim(' ', ',') }.filter { it.isNotBlank() },
                    talents = talents.map { it.trim(' ', ',') }.filter { it.isNotBlank() },
                    trappings = trappings.map { it.trim(' ', ',') }.filter { it.isNotBlank() },
                )
            }
    }

    private data class CareerLevel(
        val name: String,
        val status: SocialStatus,
        val skills: List<String>,
        val talents: List<String>,
        val trappings: List<String>,
    )

    companion object {
        private val classPageRanges = listOf(
            SocialClass.ACADEMICS to 53..60,
            SocialClass.BURGHERS to 61..68,
            SocialClass.COURTIERS to 69..76,
            SocialClass.PEASANTS to 77..84,
            SocialClass.RANGERS to 85..92,
            SocialClass.RIVERFOLK to 93..100,
            SocialClass.ROGUES to 101..108,
            SocialClass.WARRIORS to 109..116,
        )
    }
}
