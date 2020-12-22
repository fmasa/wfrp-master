package cz.frantisekmasa.wfrp_master.compendium.domain.importer

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.list.functorFilter.filter
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars.SkillListGrammar
import cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars.SpellListGrammar
import cz.frantisekmasa.wfrp_master.compendium.domain.importer.grammars.TalentListGrammar
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.InputStream
import java.util.*

class RulebookCompendiumImporter(rulebookPdf: InputStream) : CompendiumImporter, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val locale = Locale.ENGLISH
    private val reader = PdfReader(rulebookPdf)

    override suspend fun importSkills(): List<Skill> {
        val text = (118..131).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }

        try {
            return SkillListGrammar.parseToEnd(text)
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()
            Timber.e(e)

            throw e
        }
    }

    override suspend fun importTalents(): List<Talent> {
        val text = (132..147).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }

        try {
            return TalentListGrammar.parseToEnd(text)
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()
            Timber.e(e)

            throw e
        }
    }

    private fun String.dumpWithLineNumbers() {
        Timber.d(lines().mapIndexed { index, line -> "${index + 1} > $line" }.joinToString("\n"))
    }

    override suspend fun importSpells(): List<Spell> {
        val text =
            (245..257).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }
        val pettySpells =
            (240..244).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }

        return splitLoresByMagicTypes(text)
            .map(::splitSpellTextByLore)
            .fold(
                listOf("Petty Spells" to pettySpells)
            ) { allLores, magicTypeLores -> allLores + magicTypeLores }
            .map { (lore, text) ->
                try {
                    SpellListGrammar(lore).parseToEnd(text)
                } catch (e: Throwable) {
                    text.dumpWithLineNumbers()

                    Timber.e(e)
                    throw e
                }
            }
            .flatten()
    }

    private fun splitLoresByMagicTypes(text: String): List<String> {
        return text.split(
            Regex(
                "(?<=(\\n|\\. ))[a-zA-Z ]+ mA gic\n",
                RegexOption.IGNORE_CASE
            )
        )
    }

    private fun splitSpellTextByLore(text: String): List<Pair<String, String>> {
        return text.fixBrokenLoreHeadings()
            .split(Regex("\\n(?=(\\[Lore of ([a-zA-Z]+)]))", RegexOption.IGNORE_CASE))
            .mapNotNull {
                Regex("\\[Lore of ([a-zA-Z]+)]").find(it)?.let { match ->
                    match.groupValues[1] to it
                }
            }
    }

    private fun String.fixBrokenLoreHeadings(): String =
        replace(
            Regex(
                "(\\n|\\. )(The )?l ?o ?r ?e ? of ([a-zA-Z ]+?)(?=(arcane|spells|isconcernedwith|\\n))",
                RegexOption.IGNORE_CASE
            )
        ) {
            val lore = it.groupValues[3]
                .replace(" ", "")
                .toLowerCase(locale)
                .capitalize(locale)

            val prefix = if (it.groupValues[1] == ". ") ".\n" else "\n"

            "$prefix[Lore of $lore]\n"
        }


    private fun getCleanedUpTextFromPage(reader: PdfReader, page: Int): String {
        val pageText = PdfTextExtractor(reader).getTextFromPage(page, true)
            .replace("  ", " ") // There are duplicate spaces sometimes
            .replace("..", ".")
            .replace(" Range:", "\nRange:")
            .replace(" Target:", "\nTarget:")
            .replace(" Duration:", "\nDuration:")
            .replace("Y ou", "You")
            .replace("T est", "Test")
            .lines()
            .map { it.trim() }
            .dropLast(1) // Last line is always just a page number
            .filter { it != "" }
            .joinToString("\n")

        return pageText.trimStrings(
            listOf(
                "IV",
                "VIII",
                "Warhammer Fantasy r olepl ay",
                "m as T e R s kill l is T",
                "Skills and Talents", "s KI lls and talents",
                "Magic", "ma GI c",
                "\n",
                " "
            )
        )
    }

    private fun String.trimStrings(substrings: List<String>): String {
        var text = this
        while (substrings.exists { text.startsWith(it, ignoreCase = true) }) {
            substrings.forEach {
                if (text.startsWith(it, ignoreCase = true)) {
                    text = text.substring(it.length)
                }
            }
        }

        while (substrings.exists { text.endsWith(it, ignoreCase = true) }) {
            substrings.forEach {
                if (text.endsWith(it, ignoreCase = true)) {
                    text = text.substring(0, text.length - it.length)
                }
            }
        }

        return text
    }
}

