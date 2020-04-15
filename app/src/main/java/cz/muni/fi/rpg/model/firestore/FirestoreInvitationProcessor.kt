package cz.muni.fi.rpg.model.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Invitation
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreInvitationProcessor @Inject constructor(private val firestore: FirebaseFirestore) :
    InvitationProcessor {

    override suspend fun accept(userId: String, invitation: Invitation) {
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
}