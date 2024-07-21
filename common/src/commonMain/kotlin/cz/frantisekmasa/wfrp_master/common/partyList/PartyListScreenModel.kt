package cz.frantisekmasa.wfrp_master.common.partyList

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.settings.Language
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PartyListScreenModel(
    private val parties: PartyRepository,
    private val characters: CharacterRepository,
) : ScreenModel {
    fun liveForUser(userId: UserId): Flow<List<PartyListItem>> {
        return combine(
            parties.forUserLive(userId),
            characters.getPlayerCharactersInAllPartiesLive(userId),
        ) { parties, characters ->
            val charactersByParty = characters.groupBy({ it.first }) { CharacterId(it.first, it.second.id) }

            parties.map { party ->
                PartyListItem(
                    id = party.id,
                    party = party,
                    name = party.name,
                    isGameMaster = party.gameMasterId == userId || party.gameMasterId == null,
                    singleCharacterId = charactersByParty[party.id]?.singleOrNull(),
                    playersCount = party.playersCount,
                )
            }
        }
    }

    suspend fun archive(partyId: PartyId) {
        parties.update(partyId) { it.archive() }
    }

    /**
     * @throws CouldNotConnectToBackend
     */
    suspend fun createParty(
        partyName: String,
        language: Language,
        gameMasterId: UserId,
    ): PartyId {
        val partyId = PartyId.generate()
        val party =
            Party(
                id = partyId,
                name = partyName,
                settings = Settings(language = language),
                gameMasterId = gameMasterId,
                users = setOf(gameMasterId),
            )

        parties.save(party)

        Napier.d("Party $partyName was successfully created")
        Reporting.record { partyCreated(partyId) }

        return partyId
    }

    suspend fun leaveParty(
        partyId: PartyId,
        userId: UserId,
    ) {
        parties.update(partyId) { it.leave(userId) }
    }
}
