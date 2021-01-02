package cz.frantisekmasa.wfrp_master.core.domain.character

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CharacterRepository {
    suspend fun save(partyId: UUID, character: Character)

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(characterId: CharacterId): Character

    fun getLive(characterId: CharacterId): Flow<Either<CharacterNotFound, Character>>

    suspend fun hasCharacterInParty(userId: String, partyId: UUID) : Boolean

    fun inParty(partyId: UUID): Flow<List<Character>>
}