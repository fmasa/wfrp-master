package cz.frantisekmasa.wfrp_master.common.core.domain.compendium

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName

abstract class CompendiumItem<T : CompendiumItem<T>> : Parcelable {
    abstract val id: Uuid
    abstract val name: String

    abstract fun duplicate(): T

    abstract fun replace(original: T): T

    protected fun duplicateName(): String = duplicateName(name)
}
