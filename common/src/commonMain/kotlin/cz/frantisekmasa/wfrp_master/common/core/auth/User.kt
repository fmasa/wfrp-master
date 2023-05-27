package cz.frantisekmasa.wfrp_master.common.core.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("localId")
    val id: UserId,
    val email: String?,
)
