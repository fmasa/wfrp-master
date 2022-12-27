package cz.frantisekmasa.wfrp_master.common.character.effects

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent as CharacterTalent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait as CharacterTrait

sealed interface EffectSource {
    val id: Uuid
    val effects: List<CharacterEffect>

    data class Trait(val trait: CharacterTrait) : EffectSource {
        override val id: Uuid get() = trait.id
        override val effects: List<CharacterEffect> = trait.effects
    }

    data class Talent(val talent: CharacterTalent) : EffectSource {
        override val id: Uuid get() = talent.id
        override val effects: List<CharacterEffect> = talent.effects
    }
}
