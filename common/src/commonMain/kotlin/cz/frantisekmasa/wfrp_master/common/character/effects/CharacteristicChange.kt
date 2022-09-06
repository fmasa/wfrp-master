package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

open class CharacteristicChange(
    private val plus: Stats = Stats.ZERO,
    private val minus: Stats = Stats.ZERO,
) : CharacterEffect {

    override fun apply(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.updateCharacteristics(
            base = character.characteristicsBase + plus - minus,
            advances = character.characteristicsAdvances,
        )
    }

    override fun revert(character: Character, otherEffects: List<CharacterEffect>): Character {
        return character.updateCharacteristics(
            base = character.characteristicsBase + minus - plus,
            advances = character.characteristicsAdvances,
        )
    }

    companion object {
        fun fromTraitNameOrNull(name: String): CharacteristicChange? {
            val cleanName = name.lowercase()

            if (cleanName == "big") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        strength = 10,
                        toughness = 10,
                    ),
                    minus = Stats.ZERO.copy(
                        agility = 5,
                    ),
                )
            }

            if (cleanName == "brute") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        toughness = 10,
                        strength = 10,
                    ),
                    minus = Stats.ZERO.copy(
                        agility = 10,
                    ),
                )
            }

            if (cleanName == "clever") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        intelligence = 10,
                        initiative = 10,
                    ),
                )
            }

            if (cleanName == "cunning") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        intelligence = 10,
                        fellowship = 10,
                        initiative = 10,
                    )
                )
            }

            if (cleanName == "elite") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        weaponSkill = 20,
                        ballisticSkill = 20,
                        willPower = 20,
                    )
                )
            }

            if (cleanName == "leader") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        fellowship = 10,
                        willPower = 10,
                    )
                )
            }

            if (cleanName == "tough") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        toughness = 10,
                        willPower = 10,
                    )
                )
            }

            if (cleanName == "swarm") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        weaponSkill = 10,
                    )
                )
            }

            return null
        }

        fun fromTalentNameOrNull(name: String): CharacteristicChange? {
            val cleanName = name.lowercase()

            if (cleanName == "savvy") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        intelligence = 5,
                    ),
                )
            }

            if (cleanName == "suave") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        fellowship = 5,
                    ),
                )
            }

            if (cleanName == "marksman") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        ballisticSkill = 5,
                    ),
                )
            }

            if (cleanName == "very strong") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        strength = 5,
                    ),
                )
            }

            if (cleanName == "sharp") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        initiative = 5,
                    ),
                )
            }

            if (cleanName == "lightning reflexes") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        agility = 5,
                    ),
                )
            }

            if (cleanName == "coolheaded") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        willPower = 5,
                    ),
                )
            }

            if (cleanName == "very resilient") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        toughness = 5,
                    ),
                )
            }

            if (cleanName == "nimble fingered") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        dexterity = 5,
                    ),
                )
            }

            if (cleanName == "warrior born") {
                return CharacteristicChange(
                    plus = Stats.ZERO.copy(
                        weaponSkill = 5,
                    ),
                )
            }

            return null
        }
    }
}
