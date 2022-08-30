package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

class HardyWoundsModification(private val timesTaken: UInt): CharacterEffect {
    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                extraToughnessBonusMultiplier = modifiers.extraToughnessBonusMultiplier + timesTaken,
            )
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        val modifiers = character.woundsModifiers

        return character.modifyWounds(
            modifiers.copy(
                extraToughnessBonusMultiplier = (modifiers.extraToughnessBonusMultiplier - timesTaken)
                    .coerceAtLeast(0.toUInt()),
            )
        )
    }

    companion object {
        fun fromTalentOrNull(name: String, timesTaken: UInt): HardyWoundsModification? {
            if (name.equals("hardy", ignoreCase = true)) {
                return HardyWoundsModification(timesTaken)
            }

            return null
        }
    }

}