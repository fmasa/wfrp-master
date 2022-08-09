package cz.frantisekmasa.wfrp_master.common.core.auth

import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize

@Parcelize
data class UserId internal constructor(private val value: String) : Parcelable {
    companion object {
        fun fromString(userId: String): UserId = UserId(userId)
    }

    override fun toString() = value
}
