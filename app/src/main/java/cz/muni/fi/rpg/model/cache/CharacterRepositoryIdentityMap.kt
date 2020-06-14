package cz.muni.fi.rpg.model.cache

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository

internal class CharacterRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: CharacterRepository
) : CharacterRepository by inner {

    private val identityMap =
        IdentityMap<CharacterId, LiveData<Either<CharacterNotFound, Character>>>(maxEntries)

    @Synchronized
    override fun getLive(characterId: CharacterId): LiveData<Either<CharacterNotFound, Character>> {
        return identityMap.getOrPut(characterId) { inner.getLive(characterId) }
    }
}