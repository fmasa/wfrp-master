package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import arrow.core.extensions.list.foldable.exists
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.core.utils.right
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import kotlinx.coroutines.flow.*

class GameMasterViewModel(
    private val partyId: PartyId,
    private val parties: PartyRepository,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val partyFlow: Flow<Party> = parties.getLive(partyId).right()
    val party: LiveData<Party> = partyFlow.asLiveData()

    /**
     * Returns LiveData which emits either CharacterId of players current character or NULL if user
     * didn't create character yet
     */
    val players: LiveData<List<Player>?> =
        partyFlow.filterNotNull().zip(characterRepository.inParty(partyId)) { party, characters ->
            val players = characters.map { Player.ExistingCharacter(it) }
            val usersWithoutCharacter = party.users
                .filter { it != party.gameMasterId }
                .filter { userId -> !players.exists { it.character.userId == userId } }
                .map { Player.UserWithoutCharacter(it) }

            players + usersWithoutCharacter
        }.asLiveData()

    suspend fun archiveCharacter(id: CharacterId) {
        val character = characterRepository.get(id)

        character.archive()

        characterRepository.save(id.partyId, character)
    }

    suspend fun changeTime(change: (DateTime) -> DateTime) {
        val party = parties.get(partyId)

        party.changeTime(change(party.getTime()))

        parties.save(party)
    }

    suspend fun updatePartyAmbitions(ambitions: Ambitions) {
        val party = parties.get(partyId)

        party.updateAmbitions(ambitions)

        parties.save(party)
    }
}