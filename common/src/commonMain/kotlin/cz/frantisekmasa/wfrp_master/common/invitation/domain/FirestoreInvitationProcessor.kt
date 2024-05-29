package cz.frantisekmasa.wfrp_master.common.invitation.domain

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FieldValue.Companion.arrayUnion
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.serialization.Serializable

// TODO: Replace by cloud function
class FirestoreInvitationProcessor(
    private val firestore: FirebaseFirestore,
    private val parties: PartyRepository,
) : InvitationProcessor {
    override suspend fun accept(
        userId: UserId,
        invitation: Invitation,
    ): InvitationProcessingResult =
        firestore.runTransaction {
            if (isAlreadyInParty(userId, invitation.partyId)) {
                return@runTransaction InvitationProcessingResult.AlreadyInParty
            }

            firestore.collection("users")
                .document(userId.toString())
                .set(
                    strategy = InvitationsUpdate.serializer(),
                    data =
                        InvitationsUpdate(
                            invitations =
                                arrayUnion(
                                    mapOf(
                                        "partyId" to invitation.partyId.toString(),
                                        "accessCode" to invitation.accessCode,
                                    ),
                                ),
                        ),
                )

            firestore.collection("parties")
                .document(invitation.partyId.toString())
                .update("users" to arrayUnion(userId.toString()))

            return@runTransaction InvitationProcessingResult.Success
        }

    @Serializable
    private data class InvitationsUpdate(
        val invitations: FieldValue,
    )

    private suspend fun Transaction.isAlreadyInParty(
        userId: UserId,
        partyId: PartyId,
    ): Boolean {
        return try {
            parties.get(this, partyId).isMember(userId)
        } catch (e: PartyNotFound) {
            false
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }
}
