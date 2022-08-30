package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait

class TraitEffectFactory {
    fun getEffects(trait: Trait): List<CharacterEffect> {
        val name = trait.evaluatedName.trim()
        return listOfNotNull(
            SizeChange.fromTraitNameOrNull(name),
            CharacteristicChange.fromTraitNameOrNull(name),
            SwarmWoundsModification.fromTraitNameOrNull(name),
        )
    }
}