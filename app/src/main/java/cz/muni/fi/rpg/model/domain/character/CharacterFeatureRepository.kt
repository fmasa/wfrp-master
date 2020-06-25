package cz.muni.fi.rpg.model.domain.character

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import java.util.*

interface CharacterFeatureRepository<T : Any> {
    suspend fun save(characterId: CharacterId, feature: T)

    fun getLive(characterId: CharacterId): LiveData<Either<CouldNotConnectToBackend, T>>
}