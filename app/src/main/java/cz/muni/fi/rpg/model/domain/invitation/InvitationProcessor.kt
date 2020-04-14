package cz.muni.fi.rpg.model.domain.invitation

import cz.muni.fi.rpg.model.domain.party.InvitationToken

interface InvitationProcessor {
    /**
     * Gives user access to the party
     *
     * @throws InvalidInvitation
     */
    suspend fun accept(userId: String, invitation: InvitationToken)
}