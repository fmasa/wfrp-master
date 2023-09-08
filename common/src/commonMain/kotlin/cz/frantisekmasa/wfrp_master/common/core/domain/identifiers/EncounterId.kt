package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Identifier
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
@Immutable
data class EncounterId(
    val partyId: PartyId,
    @Contextual val encounterId: UUID,
) : Identifier
