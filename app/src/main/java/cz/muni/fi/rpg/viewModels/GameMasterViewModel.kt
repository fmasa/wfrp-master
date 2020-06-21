package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.*

class GameMasterViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    characterRepository: CharacterRepository
) : ViewModel() {

    val party: LiveData<Either<PartyNotFound, Party>> = parties.getLive(partyId)
    val characters: LiveData<List<Character>> = characterRepository.inParty(partyId)

    suspend fun updatePartyAmbitions(ambitions: Ambitions) {
        val party = parties.get(partyId)

        party.updateAmbitions(ambitions)

        parties.save(party)
    }
}