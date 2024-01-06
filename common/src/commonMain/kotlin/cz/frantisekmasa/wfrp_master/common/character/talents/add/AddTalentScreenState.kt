package cz.frantisekmasa.wfrp_master.common.character.talents.add

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent

@Immutable
data class AddTalentScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Talent>,
)
