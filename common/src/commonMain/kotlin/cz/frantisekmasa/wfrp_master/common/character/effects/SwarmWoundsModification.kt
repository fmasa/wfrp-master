package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

class SwarmWoundsModification : CharacterEffect {
    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        if (otherEffects.any { it is SwarmWoundsModification }) {
            return character // This effect does not stack
        }

        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                afterMultiplier = modifiers.afterMultiplier * 5,
            )
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        if (otherEffects.any { it is SwarmWoundsModification }) {
            return character // This effect does not stack
        }

        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                afterMultiplier = modifiers.afterMultiplier / 5,
            )
        )
    }

    companion object {
        fun fromTraitNameOrNull(name: String, translator: Translator): SwarmWoundsModification? {
            if (name.equals(translator.translate(Str.character_effect_swarm), ignoreCase = true)) {
                return SwarmWoundsModification()
            }

            return null
        }
    }
}
