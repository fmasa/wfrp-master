package cz.frantisekmasa.wfrp_master.common.character.effects

class EffectFactory {
    fun getEffects(item: EffectSource): List<CharacterEffect> {
        return when (item) {
            is EffectSource.Trait -> {
                val name = item.trait.evaluatedName.trim()

                listOfNotNull(
                    SizeChange.fromTraitNameOrNull(name),
                    CharacteristicChange.fromTraitNameOrNull(name),
                    SwarmWoundsModification.fromTraitNameOrNull(name),
                )
            }
            is EffectSource.Talent -> {
                val name = item.talent.name.trim()

                listOfNotNull(
                    HardyWoundsModification.fromTalentOrNull(name, item.talent.taken),
                    CharacteristicChange.fromTalentNameOrNull(name),
                )
            }
        }
    }
}