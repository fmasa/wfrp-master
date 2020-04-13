package cz.muni.fi.rpg.model.domain.party

import java.util.UUID

data class InvitationToken(
    val partyId: UUID,
    private val accessCode: String
)