package cz.frantisekmasa.wfrp_master.common.character.talents.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import javax.annotation.concurrent.Immutable

@Immutable
data class AddTalentScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Talent>,
)
