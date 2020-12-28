package cz.muni.fi.rpg.model.domain.character

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import kotlinx.coroutines.flow.Flow

interface CharacterFeatureRepository<T : Any> {
    suspend fun save(characterId: CharacterId, feature: T)

    fun getLive(characterId: CharacterId): Flow<Either<CouldNotConnectToBackend, T>>
}