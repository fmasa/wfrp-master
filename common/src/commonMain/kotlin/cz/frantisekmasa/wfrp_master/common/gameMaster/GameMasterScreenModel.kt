package cz.frantisekmasa.wfrp_master.common.gameMaster

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull

class GameMasterScreenModel(
    private val partyId: PartyId,
    private val parties: PartyRepository,
    private val characterRepository: CharacterRepository
) : ScreenModel {

    val party: Flow<Party> = parties.getLive(partyId).right()

    private val playerCharacters = characterRepository.inParty(partyId, CharacterType.PLAYER_CHARACTER)

    /**
     * Returns LiveData which emits either CharacterId of players current character or NULL if user
     * didn't create character yet
     */
    val players: Flow<List<Player>?> =
        party.filterNotNull().combineTransform(playerCharacters) { party, characters ->
            val players = characters.map { Player.ExistingCharacter(it) }
            val usersWithoutCharacter = party.players
                .filter { userId -> players.none { it.character.userId == userId.toString() } }
                .map { Player.UserWithoutCharacter(it.toString()) }

            emit(players + usersWithoutCharacter)
        }

    suspend fun archiveCharacter(id: CharacterId) {
        // TODO: Remove this character from combat (see [Combat::removeNpc()])
        val character = characterRepository.get(id)

        characterRepository.save(id.partyId, character.archive())
    }

    suspend fun changeTime(change: (DateTime) -> DateTime) {
        parties.update(partyId) {
            it.changeTime(change(it.time))
        }
    }

    suspend fun updatePartyAmbitions(ambitions: Ambitions) {
        parties.update(partyId) {
            it.updateAmbitions(ambitions)
        }
    }
}
