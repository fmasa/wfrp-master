package cz.muni.fi.rpg.model.domain.invitation

import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import java.lang.Exception


class AlreadyInParty(userId: String, partyId: PartyId)
    : Exception("User $userId is already member of party $partyId")
