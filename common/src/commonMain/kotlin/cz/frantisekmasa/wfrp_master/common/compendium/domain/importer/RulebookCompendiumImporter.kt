package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars.BlessingListGrammar
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars.MiracleListGrammar
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars.SkillListGrammar
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars.SpellListGrammar
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars.TalentListGrammar
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.InputStream
import java.util.Locale

class RulebookCompendiumImporter(rulebookPdf: InputStream) :
    CompendiumImporter,
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val locale = Locale.ENGLISH
    private val reader = PdfReader(rulebookPdf)

    override suspend fun importSkills(): List<Skill> {
        val text = (118..131).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }

        try {
            return SkillListGrammar.parseToEnd(text)
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()
            Napier.e(e.toString(), e)

            throw e
        }
    }

    override suspend fun importTalents(): List<Talent> {
        val text = (132..147).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }

        try {
            return TalentListGrammar.parseToEnd(text)
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()
            Napier.e(e.toString(), e)

            throw e
        }
    }

    private fun String.dumpWithLineNumbers() {
        Napier.d(lines().mapIndexed { index, line -> "XYZ > $line" }.joinToString("\n"))
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

                    Napier.e(e.toString(), e)
                    throw e
                }
            }
            .flatten()
    }

    override suspend fun importBlessings(): List<Blessing> {
        val text = getCleanedUpTextFromPage(reader, 221)
            .split(Regex("VII R ?e ?l ?i ?g ?i ?o ?n", RegexOption.IGNORE_CASE))[0]

        try {
            return BlessingListGrammar.parseToEnd(text)
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()

            Napier.e(e.toString(), e)
            throw e
        }
    }

    override suspend fun importMiracles(): List<Miracle> {
        val text = (222..228).joinToString("\n") { getCleanedUpTextFromPage(reader, it) }
        val miraclesByCult = splitMiraclesByCult(text)

        try {
            return miraclesByCult
                .flatMap { (cultName, text) -> MiracleListGrammar(cultName).parseToEnd(text) }
                .toList()
        } catch (e: Throwable) {
            text.dumpWithLineNumbers()

            Napier.e(e.toString(), e)
            throw e
        }
    }

    override suspend fun importTraits(): List<Trait> {
        return RulebookTraitImporter.importTraits(reader).toList()
    }

    override suspend fun importCareers(): List<Career> {
        return RulebookCareerImporter().importCareers(reader).toList()
    }

    private fun splitMiraclesByCult(text: String): Sequence<Pair<String, String>> {
        val cultNames = listOf(
            "Manann",
            "Morr",
            "Myrmidia",
            "Ranald",
            "Rhya",
            "Shallya",
            "Sigmar",
            "Taal",
            "Ulric",
            "Verena"
        )

        val buildTitle = { cultName: String -> "Miracles of $cultName" }

        var partiallyFixedText = text
        cultNames.forEach { cultName ->
            val title = buildTitle(cultName)

            partiallyFixedText = partiallyFixedText.replace(
                Regex(title.asSequence().joinToString(" ?"), RegexOption.IGNORE_CASE),
                "\n---\n$cultName\n",
            )
        }

        partiallyFixedText = partiallyFixedText
            .replace(Regex("\n +"), "\n")
            .replace("\n\n", "\n")
            .replace("\nT ", "\nT")
            .replace("\nV ", "\nV")

        return partiallyFixedText
            .split("\n---\n")
            .asSequence()
            .drop(1) // This is some general stuff before first cult's miracles
            .mapNotNull { section ->
                val (cultName, miraclesText) = section.split('\n', limit = 2)

                cultName to miraclesText
            }
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
        ) { result ->
            val lore = result.groupValues[3]
                .replace(" ", "")
                .lowercase(locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

            val prefix = if (result.groupValues[1] == ". ") ".\n" else "\n"

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
        while (substrings.any { text.startsWith(it, ignoreCase = true) }) {
            substrings.forEach {
                if (text.startsWith(it, ignoreCase = true)) {
                    text = text.substring(it.length)
                }
            }
        }

        while (substrings.any { text.endsWith(it, ignoreCase = true) }) {
            substrings.forEach {
                if (text.endsWith(it, ignoreCase = true)) {
                    text = text.substring(0, text.length - it.length)
                }
            }
        }

        return text
    }
}
