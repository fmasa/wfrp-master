package cz.frantisekmasa.wfrp_master.inventory

import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Feature
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.firestore.serialization.serializationAggregateMapper
import cz.frantisekmasa.wfrp_master.inventory.domain.Armor
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.ui.InventoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val InventoryModule = module {
    single<CharacterItemRepository<InventoryItem>> {
        FirestoreCharacterItemRepository(
            COLLECTION_INVENTORY_ITEMS,
            aggregateMapper(InventoryItem::class),
            get()
        )
    }

    single<CharacterFeatureRepository<Armor>> {
        FirestoreCharacterFeatureRepository(
            Feature.ARMOR,
            get(),
            Armor(),
            serializationAggregateMapper(),
        )
    }

    viewModel { (characterId: CharacterId) -> InventoryViewModel(characterId, get(), get(), get()) }
}
