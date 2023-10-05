package cz.frantisekmasa.wfrp_master.common.character.trappings.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping

data class AddTrappingScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Trapping>,
)
