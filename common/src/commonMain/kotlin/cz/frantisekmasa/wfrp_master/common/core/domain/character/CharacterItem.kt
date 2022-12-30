package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacterEffect
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable

interface CharacterItem<T : CharacterItem<T, C>, C : CompendiumItem<C>> : Parcelable {
    val id: Uuid
    val compendiumId: Uuid?

    @Stable
    val effects: List<CharacterEffect> get() = emptyList()

    fun updateFromCompendium(compendiumItem: C): T

    fun unlinkFromCompendium(): T
}
