package cz.frantisekmasa.wfrp_master.common.character.skills.add

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats

@Immutable
data class AddSkillScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<Skill>,
    val characteristics: Stats,
)
