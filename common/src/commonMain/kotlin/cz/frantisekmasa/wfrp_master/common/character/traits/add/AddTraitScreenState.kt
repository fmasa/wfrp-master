package cz.frantisekmasa.wfrp_master.common.character.traits.add

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait

@Immutable
data class AddTraitScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Trait>,
)
