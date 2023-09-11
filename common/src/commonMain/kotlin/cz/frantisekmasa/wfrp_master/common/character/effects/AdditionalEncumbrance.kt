package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum

class AdditionalEncumbrance(
    private val bonus: Encumbrance,
) : CharacterEffect {

    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.modifyEncumbranceBonus(
            bonus + otherEffects.filterIsInstance<AdditionalEncumbrance>()
                .map { it.bonus }
                .sum()
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.modifyEncumbranceBonus(
            otherEffects.filterIsInstance<AdditionalEncumbrance>()
                .map { it.bonus }
                .sum()
        )
    }

    companion object {
        fun fromTalentOrNull(
            name: String,
            translator: Translator,
            timesTaken: Int,
        ): AdditionalEncumbrance? {
            val cleanName = name.lowercase()

            if (cleanName == translator.translate(Str.character_effect_strong_back)) {
                return AdditionalEncumbrance(Encumbrance(timesTaken.toDouble()))
            }

            if (name.equals(translator.translate(Str.character_effect_sturdy), ignoreCase = true)) {
                return AdditionalEncumbrance(Encumbrance((timesTaken * 2).toDouble()))
            }

            return null
        }
    }
}
