package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing

data class AddBlessingScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Blessing>,
)
