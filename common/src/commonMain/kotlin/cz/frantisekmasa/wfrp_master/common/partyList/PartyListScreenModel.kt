package cz.frantisekmasa.wfrp_master.common.partyList

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

class PartyListScreenModel(
    private val parties: PartyRepository
) : ScreenModel {

    fun liveForUser(userId: String): Flow<List<Party>> {
        return parties.forUserLive(userId)
    }

    suspend fun archive(partyId: PartyId) {
        parties.update(partyId) { it.archive() }
    }

    /**
     * @throws CouldNotConnectToBackend
     */
    suspend fun createParty(partyName: String, gameMasterId: String): PartyId {
        val partyId = PartyId.generate()
        val party = Party(
            id = partyId,
            name = partyName,
            gameMasterId = gameMasterId,
            users = setOf(gameMasterId)
        )

        parties.save(party)

        Napier.d("Party $partyName was successfully created")
        Reporter.recordEvent("create_party", mapOf("id" to party.id.toString()))

        return partyId
    }

    suspend fun leaveParty(partyId: PartyId, userId: UserId) {
        parties.update(partyId) { it.leave(userId) }
    }
}
