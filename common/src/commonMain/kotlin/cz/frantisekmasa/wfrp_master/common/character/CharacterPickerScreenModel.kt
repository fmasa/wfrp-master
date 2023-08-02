package cz.frantisekmasa.wfrp_master.common.character

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class CharacterPickerScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
) : ScreenModel {

    private val playerCharacters = characters.inParty(partyId, CharacterType.PLAYER_CHARACTER)

    val unassignedPlayerCharacters = playerCharacters.map {
        it.filter { character -> character.userId == null }
    }

    suspend fun assignCharacter(character: Character, userId: UserId) {
        characters.save(partyId, character.assignToUser(userId))
    }

    fun allUserCharacters(userId: UserId): Flow<List<Character>> {
        return playerCharacters.map {
            it.filter { character ->
                character.userId == userId || character.id == userId.toString()
            }
        }
    }
}
