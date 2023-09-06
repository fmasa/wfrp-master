package cz.frantisekmasa.wfrp_master.common.core.auth

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@JvmInline
@Serializable
value class UserId internal constructor(private val value: String) : Parcelable {

    init {
        require(value.isNotBlank()) { "UserId cannot be blank" }
    }

    companion object {
        fun fromString(userId: String): UserId = UserId(userId)
    }

    override fun toString() = value
}
