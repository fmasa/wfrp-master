package cz.muni.fi.rpg.model.domain.compendium.importer.grammars

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.regexToken
import cz.muni.fi.rpg.model.domain.compendium.Talent
import org.intellij.lang.annotations.Language
import java.util.*

object TalentListGrammar : Grammar<List<Talent>>() {
    @Language("RegExp")
    private val talentNameWithMaxRegex = "\\n*([a-zA-Z -’]+)\\nMax: ([a-zA-Z0-9 ]+)"
    private val talentName by regexToken(talentNameWithMaxRegex)

    private val sentence by regexToken("((?!($talentNameWithMaxRegex))[a-záA-Z0-9 \\[\\],\\n()+\\-–—:;‘’…/!%=×&])+?[.…?\n]+")

    private val talent by talentName * oneOrMore(sentence) map { (nameWithRegex, descriptionSentences) ->
        val (name, maxTimesTaken)
            = talentNameWithMaxRegex.toRegex()
            .matchEntire(nameWithRegex.text)
            ?.destructured
            ?: error("First two talent lines should have been matched against $talentNameWithMaxRegex")

        Talent(
            id = UUID.randomUUID(),
            name = cleanupName(name),
            maxTimesTaken = maxTimesTaken,
            description = descriptionSentences.joinToString("") { it.text }.trim(),
        )

    }

    override val rootParser by skip(oneOrMore(sentence)) * oneOrMore(talent)

    private fun cleanupName(name: String) =
        name.replace("does. m as T e R Talen T l is T ", "")
            .trim()

}
