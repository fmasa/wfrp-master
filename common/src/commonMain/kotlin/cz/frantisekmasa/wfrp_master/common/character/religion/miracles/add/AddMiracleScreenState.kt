package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle

data class AddMiracleScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Miracle>,
)
