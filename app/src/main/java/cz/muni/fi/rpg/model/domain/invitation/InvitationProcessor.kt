package cz.muni.fi.rpg.model.domain.invitation

import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation

interface InvitationProcessor {
    /**
     * Gives user access to the party
     *
     * @throws InvalidInvitation
     * @throws AlreadyInParty
     */
    suspend fun accept(userId: String, invitation: Invitation)
}
