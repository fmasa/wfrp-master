package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
data class CharacterId(val partyId: PartyId, val id: String) : Parcelable
