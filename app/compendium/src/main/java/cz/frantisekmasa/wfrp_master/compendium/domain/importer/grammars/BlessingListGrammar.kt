package cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing
import java.util.*

object BlessingListGrammar : Grammar<List<Blessing>>() {
    private val blessingName by regexToken("Blessing of [a-zA-Z ]+\n")

    private val rangeLabel by literalToken("Range:")
    private val targetLabel by literalToken("Target:")
    private val durationLabel by literalToken("Duration:")

    private val newLine by literalToken("\n")
    private val dot by literalToken(".")
    private val singleLineValue by regexToken("[a-zA-Z0-9 ()]+\n")
    private val unterminatedSentence by regexToken("[a-zA-Z0-9 +,\\n()’]+")
    private val sentence by oneOrMore(unterminatedSentence or singleLineValue) *
        dot *
        skip(zeroOrMore(newLine))

    private val blessing = (
        blessingName // Blessing name
            * skip(rangeLabel) * singleLineValue // Range
            * skip(targetLabel) * singleLineValue // Target
            * skip(durationLabel) * singleLineValue // Duration
            * oneOrMore(sentence) // Effect
        ) map { (name, range, target, duration, effect) ->
        Blessing(
            UUID.randomUUID(),
            name.text.trim(),
            range.text.trim(),
            target.text.trim(),
            duration.text.trim(),
            effect.joinToString("") { (sentence, dot) -> sentence.joinToString("") { it.text } + dot.text }
                .trim(),
        )
    }

    override val rootParser = oneOrMore(blessing)
}