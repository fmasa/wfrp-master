package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import dev.icerock.moko.parcelize.Parcelable

interface CharacterItem<T : CharacterItem<T, C>, C : CompendiumItem<C>> : Parcelable {
    val id: Uuid
    val compendiumId: Uuid?

    fun updateFromCompendium(compendiumItem: C): T

    fun unlinkFromCompendium(): T
}
