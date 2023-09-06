package cz.frantisekmasa.wfrp_master.common.core.domain.party

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Invitation(
    val partyId: PartyId,
    val partyName: String,
    val accessCode: String
) : Parcelable
