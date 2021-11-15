package cz.muni.fi.rpg.model.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import java.io.InputStream

interface CharacterAvatarChanger {
    suspend fun changeAvatar(characterId: CharacterId, image: InputStream)

    suspend fun removeAvatar(characterId: CharacterId)
}