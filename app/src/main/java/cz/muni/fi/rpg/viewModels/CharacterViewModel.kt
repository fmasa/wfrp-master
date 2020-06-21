package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import kotlinx.coroutines.*

class CharacterViewModel(
    characterId: CharacterId,
    characters: CharacterRepository,
    parties: PartyRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val character = characters.getLive(characterId)

    val party: LiveData<Either<PartyNotFound, Party>> = parties.getLive(characterId.partyId)
}