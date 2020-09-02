package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.extensions.list.foldable.exists
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.CombinedLiveData
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import java.util.*

class GameMasterViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    characterRepository: CharacterRepository
) : ViewModel() {

    val party: LiveData<Either<PartyNotFound, Party>> = parties.getLive(partyId)
    val characters: LiveData<List<Character>> = characterRepository.inParty(partyId)

    /**
     * Returns LiveData with either CharacterId of players current character or NULL if user
     * didn't create character yet
     */
    fun getPlayers(): LiveData<List<Player>> {
        return Transformations
            .map(CombinedLiveData(party.right(), characters)) { (party, characters) ->

            val players = characters.map { Player.ExistingCharacter(it) }

            players +
                party.users
                    .filter { it != party.gameMasterId }
                    .filter { userId -> ! players.exists { it.character.userId == userId}}
                    .map { Player.UserWithoutCharacter(it) }
        }
    }

    suspend fun renameParty(newName: String) {
        val party = parties.get(partyId)

        party.rename(newName)

        parties.save(party)
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