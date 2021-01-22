package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterStatsViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository
) : ViewModel() {

    val character: Flow<Either<CharacterNotFound, Character>> = characters.getLive(characterId)

    fun updatePoints(mutation: (points: Points) -> Points) {
        viewModelScope.launch(Dispatchers.IO) {
            val character = characters.get(characterId)
            try {
                character.updatePoints(mutation(character.getPoints()))
                characters.save(characterId.partyId, character)
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}