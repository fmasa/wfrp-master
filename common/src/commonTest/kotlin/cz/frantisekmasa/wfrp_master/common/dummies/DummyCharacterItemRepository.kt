package cz.frantisekmasa.wfrp_master.common.dummies

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DummyCharacterItemRepository<T : CharacterItem<T, *>> : CharacterItemRepository<T> {
    private val items = mutableMapOf<CharacterId, MutableMap<Uuid, T>>()

    override fun findAllForCharacter(characterId: CharacterId): Flow<List<T>> {
        return flowOf(items.getOrDefault(characterId, emptyMap()).values.toList())
    }

    override fun getLive(
        characterId: CharacterId,
        itemId: Uuid
    ): Flow<Either<CompendiumItemNotFound, T>> {
        return flowOf(
            items[characterId]?.get(itemId).rightIfNotNull { CompendiumItemNotFound(null) }
        )
    }

    override suspend fun remove(characterId: CharacterId, itemId: Uuid) {
        items[characterId]?.remove(itemId)
    }

    override fun remove(transaction: Transaction, characterId: CharacterId, itemId: Uuid) {
        items[characterId]?.remove(itemId)
    }

    override suspend fun save(characterId: CharacterId, item: T) {
        items.getOrPut(characterId) { mutableMapOf() }[item.id] = item
    }

    override fun save(transaction: Transaction, characterId: CharacterId, item: T) {
        items.getOrPut(characterId) { mutableMapOf() }[item.id] = item
    }

    override suspend fun findByCompendiumId(
        partyId: PartyId,
        compendiumItemId: Uuid
    ): List<Pair<CharacterId, T>> {
        return items
            .asSequence()
            .filter { it.key.partyId == partyId }
            .flatMap { it.value.map { item -> it.key to item.value } }
            .toList()
    }
}
