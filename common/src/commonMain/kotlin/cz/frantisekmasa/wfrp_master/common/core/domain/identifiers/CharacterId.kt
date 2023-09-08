package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import cz.frantisekmasa.wfrp_master.common.core.domain.Identifier
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
data class CharacterId(val partyId: PartyId, val id: String) : Identifier
