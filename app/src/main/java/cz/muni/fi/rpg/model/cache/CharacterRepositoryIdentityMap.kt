package cz.muni.fi.rpg.model.cache

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
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