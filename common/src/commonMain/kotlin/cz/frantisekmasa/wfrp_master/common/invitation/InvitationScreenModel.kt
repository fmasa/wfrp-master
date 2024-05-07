package cz.frantisekmasa.wfrp_master.common.invitation

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.invitation.domain.InvitationProcessingResult
import cz.frantisekmasa.wfrp_master.common.invitation.domain.InvitationProcessor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InvitationScreenModel(
    private val invitationProcessor: InvitationProcessor,
    private val serializer: Json,
    private val parties: PartyRepository,
) : ScreenModel {
    suspend fun acceptInvitation(
        userId: UserId,
        invitation: Invitation,
    ): InvitationProcessingResult {
        return withContext(Dispatchers.IO) { invitationProcessor.accept(userId, invitation) }
    }

    fun userParties(userId: UserId): Flow<List<Party>> {
        return parties.forUserLive(userId)
    }

    suspend fun serializeInvitation(invitation: Invitation): String =
        withContext(Dispatchers.IO) {
            serializer.encodeToString(invitation)
        }

    suspend fun deserializeInvitation(json: String): Invitation? =
        withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            try {
                serializer.decodeFromString<Invitation>(json)
            } catch (e: Throwable) {
                Napier.w(e.toString(), e)

                null
            }
        }
}
