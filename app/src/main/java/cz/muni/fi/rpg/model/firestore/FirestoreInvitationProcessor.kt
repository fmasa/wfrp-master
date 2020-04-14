package cz.muni.fi.rpg.model.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.InvitationToken
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreInvitationProcessor @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val gson: Gson
) : InvitationProcessor {

    override suspend fun accept(userId: String, invitation: InvitationToken) {
        firestore.collection("users")
            .document(userId)
            .set(
                mapOf(
                    "invitations" to FieldValue.arrayUnion(
                        gson.fromJson(
                            gson.toJson(invitation),
                            Map::class.java
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