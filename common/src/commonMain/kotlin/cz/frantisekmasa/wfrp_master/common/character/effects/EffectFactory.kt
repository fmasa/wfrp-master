package cz.frantisekmasa.wfrp_master.common.character.effects

class EffectFactory {
    fun getEffects(item: EffectSource): List<CharacterEffect> {
        if (item is EffectSource.Trait) {
            val name = item.trait.evaluatedName.trim()

            return listOfNotNull(
                SizeChange.fromTraitNameOrNull(name),
                CharacteristicChange.fromTraitNameOrNull(name),
                SwarmWoundsModification.fromTraitNameOrNull(name),
            )
        }

        return emptyList()
    }
}