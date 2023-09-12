package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import java.util.Locale

/**
 * Change character size to according to largest Size(...) trait
 */
class SizeChange(private val size: Size) : CharacterEffect {

    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.changeSize(
            (otherEffects.filterIsInstance<SizeChange>() + this)
                .maxOf { it.size }
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.changeSize(
            otherEffects.filterIsInstance<SizeChange>()
                .maxOfOrNull { it.size }
        )
    }

    companion object {
        private val LANGUAGE_REGEXES = mutableMapOf<Locale, Regex>()
        fun fromTraitNameOrNull(name: String, translator: Translator): SizeChange? {
            val regex = LANGUAGE_REGEXES.getOrPut(translator.locale) {
                Regex(
                    translator.translate(Str.character_effect_size) + " \\((.*)\\)",
                    RegexOption.IGNORE_CASE,
                )
            }

            val match = regex.matchEntire(name) ?: return null

            val size = match.groupValues[1].trim()

            return Size.values()
                .firstOrNull {
                    translator.translate(it.translatableName).equals(size, ignoreCase = true)
                }?.let { SizeChange(it) }
        }
    }
}
