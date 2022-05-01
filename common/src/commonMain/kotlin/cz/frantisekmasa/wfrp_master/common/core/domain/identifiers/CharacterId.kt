package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize

@Parcelize
data class CharacterId(val partyId: PartyId, val id: String) : Parcelable
