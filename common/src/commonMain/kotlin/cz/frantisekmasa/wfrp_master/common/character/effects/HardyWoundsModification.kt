package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

class HardyWoundsModification(private val timesTaken: Int) : CharacterEffect {
    init {
        require(timesTaken > 0)
    }

    override fun apply(
        character: Character,
        otherEffects: List<CharacterEffect>,
    ): Character {
        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                extraToughnessBonusMultiplier = modifiers.extraToughnessBonusMultiplier + timesTaken,
            ),
        )
    }

    override fun revert(
        character: Character,
        otherEffects: List<CharacterEffect>,
    ): Character {
        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                extraToughnessBonusMultiplier =
                    (modifiers.extraToughnessBonusMultiplier - timesTaken)
                        .coerceAtLeast(0),
            ),
        )
    }

    companion object {
        fun fromTalentOrNull(
            name: String,
            translator: Translator,
            timesTaken: Int,
        ): HardyWoundsModification? {
            val effectName = translator.translate(Str.character_effect_hardy)

            if (name.equals(effectName, ignoreCase = true)) {
                return HardyWoundsModification(timesTaken)
            }

            return null
        }
    }
}
