package cz.frantisekmasa.wfrp_master.common.core.domain.character

import arrow.core.Either
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun save(partyId: PartyId, character: Character)

    fun save(transaction: Transaction, partyId: PartyId, character: Character)

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(characterId: CharacterId): Character

    fun getLive(characterId: CharacterId): Flow<Either<CharacterNotFound, Character>>

    suspend fun hasCharacterInParty(userId: String, partyId: PartyId): Boolean

    suspend fun findByCompendiumCareer(partyId: PartyId, careerId: Uuid): List<Character>

    fun inParty(partyId: PartyId, type: CharacterType): Flow<List<Character>> {
        return inParty(partyId, setOf(type))
    }

    fun inParty(partyId: PartyId, types: Set<CharacterType>): Flow<List<Character>>
}
