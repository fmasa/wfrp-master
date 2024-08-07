package cz.frantisekmasa.wfrp_master.common.dummies

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DummyCharacterRepository : CharacterRepository {
    private val characters = mutableMapOf<PartyId, MutableMap<String, Character>>()

    override suspend fun save(
        partyId: PartyId,
        character: Character,
    ) {
        characters.getOrPut(partyId) { mutableMapOf() }[character.id] = character
    }

    override fun save(
        transaction: Transaction,
        partyId: PartyId,
        character: Character,
    ) {
        characters.getOrPut(partyId) { mutableMapOf() }[character.id] = character
    }

    override suspend fun get(characterId: CharacterId): Character {
        return characters[characterId.partyId]?.get(characterId.id)
            ?: throw CharacterNotFound(characterId)
    }

    override fun findByIds(
        partyId: PartyId,
        characterIds: Set<LocalCharacterId>,
    ): Flow<Map<LocalCharacterId, Character>> {
        return flowOf(
            characters[partyId]
                ?.filterKeys { it in characterIds }
                ?: emptyMap(),
        )
    }

    override fun getLive(characterId: CharacterId): Flow<Either<CharacterNotFound, Character>> {
        return flowOf(
            characters[characterId.partyId]?.get(characterId.id)
                .rightIfNotNull { CharacterNotFound(characterId) },
        )
    }

    override fun getPlayerCharactersInAllPartiesLive(userId: UserId): Flow<List<Pair<PartyId, Character>>> {
        return flowOf(
            characters.entries
                .asSequence()
                .flatMap { (partyId, characters) ->
                    characters
                        .values
                        .asSequence()
                        .filter { it.userId == userId }
                        .map { partyId to it }
                }
                .toList(),
        )
    }

    override suspend fun findByCompendiumCareer(
        partyId: PartyId,
        careerId: Uuid,
    ): List<Character> {
        return characters[partyId]
            ?.values
            ?.filter { it.compendiumCareer?.careerId == careerId } ?: emptyList()
    }

    override fun inParty(
        partyId: PartyId,
        types: Set<CharacterType>,
    ): Flow<List<Character>> {
        return flowOf(
            characters[partyId]
                ?.values
                ?.filter { it.type in types } ?: emptyList(),
        )
    }
}
