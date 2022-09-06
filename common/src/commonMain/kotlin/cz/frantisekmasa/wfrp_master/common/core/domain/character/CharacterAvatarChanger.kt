package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

interface CharacterAvatarChanger {
    suspend fun changeAvatar(characterId: CharacterId, image: ByteArray)

    suspend fun removeAvatar(characterId: CharacterId)
}
