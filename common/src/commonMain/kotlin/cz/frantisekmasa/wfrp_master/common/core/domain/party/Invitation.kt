package cz.frantisekmasa.wfrp_master.common.core.domain.party

import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Invitation(
    val partyId: PartyId,
    val partyName: String,
    val accessCode: String
) : Parcelable
