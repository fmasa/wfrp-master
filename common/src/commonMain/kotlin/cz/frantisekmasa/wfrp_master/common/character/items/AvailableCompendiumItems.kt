package cz.frantisekmasa.wfrp_master.common.character.items

import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import kotlinx.collections.immutable.ImmutableList
import javax.annotation.concurrent.Immutable

@Immutable
data class AvailableCompendiumItems<T : CompendiumItem<T>>(
    val availableCompendiumItems: ImmutableList<T>,
    val isCompendiumEmpty: Boolean,
)
