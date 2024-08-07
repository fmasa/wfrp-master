package cz.frantisekmasa.wfrp_master.common.core.domain.character

import arrow.core.Either
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun save(
        partyId: PartyId,
        character: Character,
    )

    fun save(
        transaction: Transaction,
        partyId: PartyId,
        character: Character,
    )

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(characterId: CharacterId): Character

    /**
     * Returns map of characters with given ids.
     * If character with given id is not found, it is not included in the result.
     */
    fun findByIds(
        partyId: PartyId,
        characterIds: Set<LocalCharacterId>,
    ): Flow<Map<LocalCharacterId, Character>>

    fun getLive(characterId: CharacterId): Flow<Either<CharacterNotFound, Character>>

    fun getPlayerCharactersInAllPartiesLive(userId: UserId): Flow<List<Pair<PartyId, Character>>>

    suspend fun findByCompendiumCareer(
        partyId: PartyId,
        careerId: Uuid,
    ): List<Character>

    fun inParty(
        partyId: PartyId,
        type: CharacterType,
    ): Flow<List<Character>> {
        return inParty(partyId, setOf(type))
    }

    fun inParty(
        partyId: PartyId,
        types: Set<CharacterType>,
    ): Flow<List<Character>>
}
