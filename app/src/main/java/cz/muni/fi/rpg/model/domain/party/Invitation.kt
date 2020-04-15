package cz.muni.fi.rpg.model.domain.party

import java.util.UUID

data class Invitation(
    val partyId: UUID,
    val partyName: String,
    val accessCode: String
)