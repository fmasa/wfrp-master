package cz.muni.fi.rpg.model.domain.character

import androidx.lifecycle.LiveData
import arrow.core.Either
import java.util.UUID

interface CharacterRepository {
    suspend fun save(partyId: UUID, character: Character)

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(characterId: CharacterId): Character

    fun getLive(characterId: CharacterId): LiveData<Either<CharacterNotFound, Character>>

    suspend fun hasCharacterInParty(userId: String, partyId: UUID) : Boolean

    fun inParty(partyId: UUID): LiveData<List<Character>>
}