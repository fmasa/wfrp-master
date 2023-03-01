package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction

interface CharacterCompendiumItemRepository<T> {
    fun save(transaction: Transaction, characterId: CharacterId, item: T)

    suspend fun findByCompendiumId(
        partyId: PartyId,
        compendiumItemId: Uuid,
    ): List<Pair<CharacterId, T>>
}
