package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.regexToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import org.intellij.lang.annotations.Language
import java.util.UUID

object TalentListGrammar : Grammar<List<Talent>>() {
    @Language("RegExp")
    private val talentNameWithMaxRegex =
        "\\n*([a-zA-Z -’]+)\\nMax(imum)?: ([a-záA-Z0-9 \\[\\],()+\\-–—:;‘’…/!%=×&]+)"
    private val talentName by regexToken(talentNameWithMaxRegex)

    private val sentence by regexToken("((?!($talentNameWithMaxRegex))[a-záA-Z0-9 \\[\\],\\n()+\\-–—:;‘’…/!%=×&])+?[.…?\n]+")

    private val talent by talentName * oneOrMore(sentence) map { (nameWithRegex, descriptionSentences) ->
        val (name, _, maxTimesTaken) =
            talentNameWithMaxRegex.toRegex()
                .matchEntire(nameWithRegex.text)
                ?.destructured
                ?: error("First two talent lines should have been matched against $talentNameWithMaxRegex")

        val (tests, description) = splitTestsAndDescription(
            descriptionSentences
                .joinToString("") { it.text }.trim()
                .replace(" at\n", " at ")
        )

        Talent(
            id = UUID.randomUUID(),
            name = cleanupName(name),
            tests = tests,
            maxTimesTaken = maxTimesTaken,
            description = description,
        )
    }

    private val testsLabels = listOf("Tests: ", "Bonus Tests: ")

    override val rootParser by skip(oneOrMore(sentence)) * oneOrMore(talent)

    private fun splitTestsAndDescription(text: String): Pair<String, String> {
        if (testsLabels.none { text.startsWith(it) }) {
            return Pair("", text)
        }

        val lines = text.lines()
        val testLinesCount = if (lines[1].length < 50) 2 else 1

        val tests = lines.slice(0 until testLinesCount).joinToString(" ")
            .split(':', limit = 2)[1]
            .trim()

        return Pair(
            tests,
            lines
                .asSequence()
                .drop(testLinesCount)
                .joinToString("\n")
        )
    }

    private fun cleanupName(name: String) =
        name.replace("does. m as T e R Talen T l is T ", "")
            // Names starting by "T" have extra space after T for some reason
            .replace(Regex("^(T )"), "T")
            .trim()
}
