package cz.muni.fi.rpg.model.cache

import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import kotlinx.coroutines.flow.Flow

internal class CharacterRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: CharacterRepository
) : CharacterRepository by inner {

    private val identityMap =
        IdentityMap<CharacterId, Flow<Either<CharacterNotFound, Character>>>(maxEntries)

    @Synchronized
    override fun getLive(characterId: CharacterId) =
        identityMap.getOrPut(characterId) { inner.getLive(characterId) }
}