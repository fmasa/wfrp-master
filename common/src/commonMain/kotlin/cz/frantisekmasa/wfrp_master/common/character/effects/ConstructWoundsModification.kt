package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

class ConstructWoundsModification : CharacterEffect {
    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        if (otherEffects.any { it is ConstructWoundsModification }) {
            return character // This effect does not stack
        }

        return character.modifyWounds(
            character.woundsModifiers.copy(isConstruct = true)
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        if (otherEffects.any { it is ConstructWoundsModification }) {
            return character // This effect does not stack
        }

        return character.modifyWounds(
            character.woundsModifiers.copy(isConstruct = false)
        )
    }

    companion object {
        fun fromTraitNameOrNull(
            name: String,
            translator: Translator,
        ): ConstructWoundsModification? {
            if (name.equals(translator.translate(Str.character_effect_construct), ignoreCase = true)) {
                return ConstructWoundsModification()
            }

            return null
        }
    }
}
