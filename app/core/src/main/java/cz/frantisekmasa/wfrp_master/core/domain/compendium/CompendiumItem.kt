package cz.frantisekmasa.wfrp_master.core.domain.compendium

import android.os.Parcelable
import java.util.UUID

abstract class CompendiumItem<T : CompendiumItem<T>> : Parcelable {
    abstract val id: UUID
    abstract val name: String

    abstract fun duplicate(): T

    protected fun duplicateName(): String = "$name (Copy)"
}
