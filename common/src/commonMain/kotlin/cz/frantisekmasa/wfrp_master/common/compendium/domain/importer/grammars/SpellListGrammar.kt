package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.regexToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import java.util.UUID

class SpellListGrammar(private val loreName: String) : Grammar<List<Spell>>() {
    private val spellNameWithCastingNumber by regexToken("\\n*[a-zA-Zâ ’\\-]+\\nCN: \\d+")

    private val range by regexToken("[\\n ]Range: ([a-zA-Z0-9 ’]+)(?=\n)")
    private val target by regexToken("[\\n ]Target: ([a-zA-Z0-9 ()’,]+)(?=\n)")
    private val duration by regexToken("[\\n ]Duration: ([a-zA-Z0-9 +’]+)\n")

    private val sentence by regexToken("((?!CN:)(?!Range:)(?!Target:)(?!Duration:)[a-zA-Z0-9 \\[\\],\\n()+\\-–—:;‘’…/!])+?[.\\n]+[ ]*")

    private val effect = oneOrMore(sentence) map { it.joinToString(separator = "") { sentence -> sentence.text } }

    private val spell by
    spellNameWithCastingNumber *
        range *
        target *
        duration *
        effect map { (nameWithCastingNumber, range, target, duration, effect) ->
        val parts = nameWithCastingNumber.text.split("\nCN: ")

        Spell(
            id = UUID.randomUUID(),
            name = cleanupName(parts[0]),
            castingNumber = parts[1].toUInt(),
            range = extractTextValue(range),
            target = extractTextValue(target),
            duration = extractTextValue(duration),
            effect = effect.trim(),
            lore = loreName,
        )
    }

    private fun extractTextValue(value: TokenMatch) = value.text.split(':', limit = 2)[1].trim()

    private fun cleanupName(name: String) =
        // Names starting by "T" have extra space after T for some reason
        name.replace(Regex("^(T )"), "T")
            .trim()

    override val rootParser = skip(zeroOrMore(sentence)) *
        oneOrMore(spell)
}
