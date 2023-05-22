package cz.frantisekmasa.wfrp_master.common.invitation.domain

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation

interface InvitationProcessor {
    /**
     * Gives user access to the party
     */
    suspend fun accept(userId: UserId, invitation: Invitation): InvitationProcessingResult
}

sealed interface InvitationProcessingResult {
    object Success : InvitationProcessingResult
    object AlreadyInParty : InvitationProcessingResult
    data class InvalidInvitation(val message: String, val cause: Throwable?) : InvitationProcessingResult
}
