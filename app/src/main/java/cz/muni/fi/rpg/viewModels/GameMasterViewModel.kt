package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull

class GameMasterViewModel(
    private val partyId: PartyId,
    private val parties: PartyRepository,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    val party: Flow<Party> = parties.getLive(partyId).right()

    /**
     * Returns LiveData which emits either CharacterId of players current character or NULL if user
     * didn't create character yet
     */
    val players: Flow<List<Player>?> =
        party.filterNotNull().combineTransform(characterRepository.inParty(partyId)) { party, characters ->
            val players = characters.map { Player.ExistingCharacter(it) }
            val usersWithoutCharacter = party.players
                .filter { userId -> players.none { it.character.userId == userId.toString() } }
                .map { Player.UserWithoutCharacter(it.toString()) }

            emit(players + usersWithoutCharacter)
        }

    suspend fun archiveCharacter(id: CharacterId) {
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
