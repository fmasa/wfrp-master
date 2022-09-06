package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

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
        fun fromTraitNameOrNull(name: String): SizeChange? {
            val match = Regex("Size \\((.*)\\)", RegexOption.IGNORE_CASE).matchEntire(name)
                ?: return null

            val size = match.groupValues[1].trim()

            return Size.values().firstOrNull { it.name.equals(size, ignoreCase = true) }
                ?.let { SizeChange(it) }
        }
    }
}
