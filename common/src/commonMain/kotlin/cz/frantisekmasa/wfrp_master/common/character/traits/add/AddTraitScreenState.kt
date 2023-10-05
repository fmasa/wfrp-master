package cz.frantisekmasa.wfrp_master.common.character.traits.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import javax.annotation.concurrent.Immutable

@Immutable
data class AddTraitScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Trait>,
)
