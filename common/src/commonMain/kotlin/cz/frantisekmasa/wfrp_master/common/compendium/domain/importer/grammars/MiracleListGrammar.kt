package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.grammars

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import java.util.UUID

class MiracleListGrammar(private val cultName: String) {
    fun parseToEnd(text: String): List<Miracle> {
        return text
            .replace(Regex("\\n([a-z ’-]+)\\nRange:", RegexOption.IGNORE_CASE), "\n---\n$1\nRange:")
            .split("\n---\n")
            .map { miracleText ->
                val lines = miracleText.lines()

                Miracle(
                    UUID.randomUUID(),
                    name = lines[0],
                    range = lines[1].split(':', limit = 2)[1].trim(),
                    target = lines[2].split(':', limit = 2)[1].trim(),
                    duration = lines[3].split(':', limit = 2)[1].trim(),
                    effect = lines.drop(4).joinToString("\n"),
                    cultName = cultName,
                )
            }
    }
}
