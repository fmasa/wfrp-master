package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
data class EncounterId(
    val partyId: PartyId,
    @Contextual val encounterId: UUID,
) : Parcelable
