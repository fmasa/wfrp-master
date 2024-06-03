package cz.frantisekmasa.wfrp_master.common.character.spells

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import kotlinx.collections.immutable.ImmutableList

data class SpellsScreenState(
    val spellGroups: ImmutableList<SpellGroup>,
)

data class SpellGroup(
    val lore: SpellLore?,
    val spells: ImmutableList<SpellDataItem>,
)

data class SpellDataItem(
    val id: Uuid,
    val name: String,
    val castingNumber: Int,
    val isMemorized: Boolean,
)
