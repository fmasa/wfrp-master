package cz.muni.fi.rpg.model.domain.invitation

import java.lang.Exception
import java.util.*

class AlreadyInParty(userId: String, partyId: UUID)
    : Exception("User $userId is already member of party $partyId")
