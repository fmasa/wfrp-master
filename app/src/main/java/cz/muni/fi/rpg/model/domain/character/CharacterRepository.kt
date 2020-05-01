package cz.muni.fi.rpg.model.domain.character

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.party.Party
import java.util.UUID

interface CharacterRepository {
    suspend fun save(partyId: UUID, character: Character)

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(partyId: UUID, userId: String): Character

    fun getLive(partyId: UUID, userId: String): LiveData<Either<CharacterNotFound, Character>>

    suspend fun hasCharacterInParty(userId: String, partyId: UUID) : Boolean

    fun inParty(partyId: UUID): LiveData<List<Character>>
}