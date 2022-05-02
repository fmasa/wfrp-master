package cz.frantisekmasa.wfrp_master.common.character

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterPickerScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
): ScreenModel {
    fun allUserCharacters(userId: UserId): Flow<List<Character>> {
        return characters.inParty(partyId).map {
            it.filter { character ->
                character.userId == userId.toString() || character.id == userId.toString()
            }
        }
    }
}