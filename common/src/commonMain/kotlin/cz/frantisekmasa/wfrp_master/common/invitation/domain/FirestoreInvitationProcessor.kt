package cz.frantisekmasa.wfrp_master.common.invitation.domain

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.arrayUnion


class FirestoreInvitationProcessor(
    private val firestore: Firestore,
    private val parties: PartyRepository
) : InvitationProcessor {

    override suspend fun accept(userId: String, invitation: Invitation): InvitationProcessingResult {
        if (isAlreadyInParty(userId, invitation.partyId)) {
            return InvitationProcessingResult.AlreadyInParty
        }

        firestore.collection("users")
            .document(userId)
            .set(
                mapOf(
                    "invitations" to arrayUnion(
                        mapOf(
                            "partyId" to invitation.partyId.toString(),
                            "accessCode" to invitation.accessCode
                        )
                    )
                ),
                SetOptions.MERGE
            )

        firestore.collection("parties")
            .document(invitation.partyId.toString())
            .update("users", arrayUnion(userId))

        return InvitationProcessingResult.Success
    }

    private suspend fun isAlreadyInParty(userId: String, partyId: PartyId): Boolean {
        return try {
            parties.get(partyId).isMember(UserId.fromString(userId))
        } catch (e: PartyNotFound) {
            false
        }
    }
}
