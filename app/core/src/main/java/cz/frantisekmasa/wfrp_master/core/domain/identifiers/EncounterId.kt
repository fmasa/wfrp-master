package cz.frantisekmasa.wfrp_master.core.domain.identifiers

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
data class EncounterId(
    val partyId: PartyId,
    @Contextual val encounterId: UUID,
) : Parcelable
