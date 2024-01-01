package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Identifier
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class EncounterId(
    val partyId: PartyId,
    val encounterId: UuidAsString,
) : Identifier
