package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.localization.Translator

class CorruptionPointsBufferBonus(
    private val bonus: Int,
) : CharacterEffect {
    override fun apply(
        character: Character,
        otherEffects: List<CharacterEffect>,
    ): Character {
        return character.modifyCorruptionBufferBonus(
            bonus +
                otherEffects.filterIsInstance<CorruptionPointsBufferBonus>().sumOf { it.bonus },
        )
    }

    override fun revert(
        character: Character,
        otherEffects: List<CharacterEffect>,
    ): Character {
        return character.modifyCorruptionBufferBonus(
            otherEffects.filterIsInstance<CorruptionPointsBufferBonus>()
                .sumOf { it.bonus },
        )
    }

    companion object {
        fun fromTalentOrNull(
            name: String,
            translator: Translator,
            timesTaken: Int,
        ): CorruptionPointsBufferBonus? {
            if (name.equals(translator.translate(Str.character_effect_pure_soul), ignoreCase = true)) {
                return CorruptionPointsBufferBonus(timesTaken)
            }

            return null
        }
    }
}
