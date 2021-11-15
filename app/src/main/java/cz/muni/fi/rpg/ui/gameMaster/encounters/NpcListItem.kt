package cz.muni.fi.rpg.ui.gameMaster.encounters

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId

data class NpcListItem(
    val id: NpcId,
    val name: String,
    val alive: Boolean,
)
