package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.Uuid
import dev.icerock.moko.parcelize.Parcelable

sealed class CompendiumItem<T : CompendiumItem<T>> : Parcelable {
    abstract val id: Uuid
    abstract val name: String
    abstract val isVisibleToPlayers: Boolean

    abstract fun duplicate(): T

    abstract fun replace(original: T): T

    abstract fun changeVisibility(isVisibleToPlayers: Boolean): T
}
