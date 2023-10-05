package cz.frantisekmasa.wfrp_master.common.character.spells.add

import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell

data class AddSpellScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Spell>,
)
