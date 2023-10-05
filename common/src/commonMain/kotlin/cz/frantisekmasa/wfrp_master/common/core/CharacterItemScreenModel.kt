package cz.frantisekmasa.wfrp_master.common.core

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class CharacterItemScreenModel<T : CharacterItem<T, *>>(
    val characterId: CharacterId,
    private val repository: CharacterItemRepository<T>,
    private val userProvider: UserProvider,
    partyRepository: PartyRepository,
) : ScreenModel {

    val isGameMaster = partyRepository.getLive(characterId.partyId)
        .right()
        .map { it.gameMasterId == userProvider.userId }
        .distinctUntilChanged()

    fun getItem(itemId: Uuid): Flow<Either<CompendiumItemNotFound, T>> {
        return repository.getLive(characterId, itemId)
    }

    open suspend fun saveItem(item: T) {
        withContext(Dispatchers.IO) {
            repository.save(characterId, item)
        }
    }

    open suspend fun removeItem(item: T) {
        withContext(Dispatchers.IO) {
            repository.remove(characterId, item.id)
        }
    }
}
