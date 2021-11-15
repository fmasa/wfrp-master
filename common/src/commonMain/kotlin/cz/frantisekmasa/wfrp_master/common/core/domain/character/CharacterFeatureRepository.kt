package cz.frantisekmasa.wfrp_master.common.core.domain.character

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow

interface CharacterFeatureRepository<T : Any> {
    suspend fun save(characterId: CharacterId, feature: T)

    fun getLive(characterId: CharacterId): Flow<Either<CouldNotConnectToBackend, T>>
}
