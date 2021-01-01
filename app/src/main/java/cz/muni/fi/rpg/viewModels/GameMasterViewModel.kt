package cz.muni.fi.rpg.viewModels

import arrow.core.extensions.list.foldable.exists
import cz.muni.fi.rpg.model.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.core.utils.right
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import java.util.*

class GameMasterViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    private val characterRepository: CharacterRepository
) {

    val party: Flow<Party> = parties.getLive(partyId).right()
    val characters: Flow<List<Character>> = characterRepository.inParty(partyId)

    /**
     * Returns flow which emits either CharacterId of players current character or NULL if user
     * didn't create character yet
     */
    fun getPlayers(): Flow<List<Player>> {
        return party.zip(characters) { party, characters ->
            val players = characters.map { Player.ExistingCharacter(it) }
            val usersWithoutCharacter = party.users
                .filter { it != party.gameMasterId }
                .filter { userId -> !players.exists { it.character.userId == userId } }
                .map { Player.UserWithoutCharacter(it) }

            players + usersWithoutCharacter
        }
    }

    suspend fun archiveCharacter(id: CharacterId) {
        val character = characterRepository.get(id)

        character.archive()

        characterRepository.save(id.partyId, character)
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