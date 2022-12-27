package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName

sealed class CompendiumItem<T : CompendiumItem<T>> : Parcelable {
    abstract val id: Uuid
    abstract val name: String

    abstract fun duplicate(): T

    abstract fun replace(original: T): T

    protected fun duplicateName(): String = duplicateName(name)
}
