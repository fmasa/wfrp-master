package cz.muni.fi.rpg.model.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.invitation.AlreadyInParty
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import kotlinx.coroutines.tasks.await

class FirestoreInvitationProcessor(
    private val firestore: FirebaseFirestore,
    private val parties: PartyRepository
) : InvitationProcessor {

    override suspend fun accept(userId: String, invitation: Invitation) {
        if (isAlreadyInParty(userId, invitation.partyId)) {
            throw AlreadyInParty(userId, invitation.partyId)
        }

        firestore.collection("users")
            .document(userId)
            .set(
                mapOf(
                    "invitations" to FieldValue.arrayUnion(
                        mapOf(
                            "partyId" to invitation.partyId.toString(),
                            "accessCode" to invitation.accessCode
                        )
                    )
                ),
                SetOptions.merge()
            )
            .await()

        firestore.collection("parties")
            .document(invitation.partyId.toString())
            .update("users", FieldValue.arrayUnion(userId))
            .await()
    }

    private suspend fun isAlreadyInParty(userId: String, partyId: PartyId): Boolean {
        return try {
            parties.get(partyId).isMember(UserId.fromString(userId))
        } catch (e: PartyNotFound) {
            false
        }
    }
}
