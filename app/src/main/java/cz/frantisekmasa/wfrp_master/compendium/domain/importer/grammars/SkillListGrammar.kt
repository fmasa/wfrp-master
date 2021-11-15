package cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.regexToken
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import org.intellij.lang.annotations.Language
import java.util.UUID

private val characteristicShortcuts = Characteristic
    .values()
    .map { it.getShortcutName() to it }
    .toMap()

private val characteristicShortcutRegex = characteristicShortcuts.keys.joinToString("|")

object SkillListGrammar : Grammar<List<Skill>>() {
    @Language("RegExp")
    private val skillNameLineRegex =
        "\\n*([a-zA-Z ]+) \\(($characteristicShortcutRegex)\\) (basic|advanced)(, grouped)?\\n"

    @Language("RegExp")
    private const val specialisationsRegex = "\\n*Specialisations: (([a-zA-Z \\-–—]+)(,\n?[a-zA-Z \\-–—]+)*)\\n*"

    private val skillNameLine by regexToken(skillNameLineRegex)
    private val specialisationsLine by regexToken(specialisationsRegex)
    private val sentence by regexToken("((?!($skillNameLineRegex))(?!Specialisations:)[a-záA-Z0-9 \\[\\],\\n()+\\-–—:;‘’…/!%=×&])+?[.…?\n]+")

    private val skill by skillNameLine *
        oneOrMore(sentence) *
        optional(specialisationsLine * skip(zeroOrMore(sentence))) map { (skillNameLine, descriptionSentences, specialisations) ->
        val (name, characteristic, basicOrAdvanced) =
            skillNameLineRegex.toRegex()
                .matchEntire(skillNameLine.text)
                ?.destructured
                ?: error("Skill name should have been matched against $skillNameLineRegex")

        val suffixes = if (specialisations == null)
            listOf("")
        else specialisationsRegex.toRegex().matchEntire(specialisations.text)?.let { result ->
            result.groupValues[1]
                .split(Regex(", ?"))
                .map {
                    it.replace("o pt I ons", "")
                        .trim()
                }
                .map { " ($it)" }
        } ?: error("Specialisations should be matched")

        suffixes.map { suffix ->
            Skill(
                id = UUID.randomUUID(),
                name = cleanupName("${name.trim()}$suffix"),
                description = cleanupDescription(descriptionSentences.joinToString("") { it.text }),
                characteristic = characteristicShortcuts[characteristic]
                    ?: error("Invalid characteristic $characteristic"),
                advanced = basicOrAdvanced == "advanced",
            )
        }
    }

    override val rootParser by skip(oneOrMore(sentence)) * oneOrMore(skill) map { it.flatten() }

    private fun cleanupName(name: String) =
        name.replace("m as T e R s kill l is T ", "")
            // Names starting by "T" have extra space after T for some reason
            .replace(Regex("^(T )"), "T")

    private fun cleanupDescription(description: String) =
        description
            .split( // Remove Info boxes
                "p u B l I c s pea KI n G",
                "Opti O ns",
                "h al F -heard Wh I spers",
                "\nExample:",
            )[0]
            .trim()
}
