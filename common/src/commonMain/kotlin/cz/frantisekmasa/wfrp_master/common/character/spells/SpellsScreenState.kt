package cz.frantisekmasa.wfrp_master.common.character.spells

import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import kotlinx.collections.immutable.ImmutableList

data class SpellsScreenState(
    val spells: ImmutableList<Spell>,
)
