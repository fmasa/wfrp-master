package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Invitation

class JoinPartyViewModel(private val invitationProcessor: InvitationProcessor) : ViewModel() {

    suspend fun acceptInvitation(userId: String, invitation: Invitation) {
        invitationProcessor.accept(userId, invitation)
    }
}