package cz.frantisekmasa.wfrp_master.common.compendium.trapping

import arrow.core.Either
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournalProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TrappingCompendiumScreenModel(
    private val partyId: PartyId,
    firestore: FirebaseFirestore,
    compendium: Compendium<Trapping>,
    characterItems: CharacterItemRepository<InventoryItem>,
    parties: PartyRepository,
    private val trappingJournalProvider: TrappingJournalProvider,
) : CharacterItemCompendiumItemScreenModel<Trapping, InventoryItem>(
        partyId,
        firestore,
        compendium,
        characterItems,
        parties,
    ) {
    override suspend fun updateCharacterItem(
        transaction: Transaction,
        party: Party,
        characterId: CharacterId,
        existing: InventoryItem,
        new: InventoryItem,
    ) {
        characterItems.save(transaction, characterId, new)
    }

    fun getTrappingDetail(trappingId: Uuid): Flow<Either<CompendiumItemNotFound, CompendiumTrappingDetailScreenState>> {
        return combine(
            get(trappingId),
            trappingJournalProvider.getTrappingJournal(partyId),
        ) { trappingOrError, trappingJournal ->
            trappingOrError.map {
                CompendiumTrappingDetailScreenState(
                    item = it,
                    trappingJournal = trappingJournal,
                )
            }
        }
    }
}
