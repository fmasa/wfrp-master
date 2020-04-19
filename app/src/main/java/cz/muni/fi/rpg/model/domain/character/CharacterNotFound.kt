package cz.muni.fi.rpg.model.domain.character

import java.util.*

class CharacterNotFound(userId: String, partyId: UUID, cause: Throwable? = null) :
    Exception("Character for user $userId was not found in party $partyId", cause)