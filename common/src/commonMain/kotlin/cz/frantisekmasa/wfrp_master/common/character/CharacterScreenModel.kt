package cz.frantisekmasa.wfrp_master.common.character

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

class CharacterScreenModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    private val avatarChanger: CharacterAvatarChanger,
) : ScreenModel {

    val character: Flow<Character> = characters.getLive(characterId).right()

    suspend fun update(change: (Character) -> Character) {
        val character = characters.get(characterId)

        characters.save(characterId.partyId, change(character))
    }

    suspend fun characterExists(): Boolean {
        return try {
            characters.get(characterId)

            true
        } catch (e: CharacterNotFound) {
            false
        }
    }

    suspend fun changeAvatar(image: ByteArray) {
        avatarChanger.changeAvatar(characterId, image)
    }

    suspend fun removeAvatar() {
        avatarChanger.removeAvatar(characterId)
    }
}
