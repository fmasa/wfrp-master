package cz.frantisekmasa.wfrp_master.religion

import cz.frantisekmasa.wfrp_master.compendium.infrastructure.FirestoreCompendium
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.frantisekmasa.wfrp_master.religion.ui.blessings.BlessingsViewModel
import cz.frantisekmasa.wfrp_master.religion.ui.miracles.MiraclesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

val ReligionModule = module {

    fun Scope.blessingCompendium() = FirestoreCompendium(
        COLLECTION_COMPENDIUM_BLESSINGS,
        get(),
        aggregateMapper(cz.frantisekmasa.wfrp_master.compendium.domain.Blessing::class),
    )

    fun Scope.miracleCompendium() = FirestoreCompendium(
        COLLECTION_COMPENDIUM_MIRACLES,
        get(),
        aggregateMapper(cz.frantisekmasa.wfrp_master.compendium.domain.Miracle::class),
    )

    single<CharacterItemRepository<Blessing>>(named(Service.BLESSING_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_BLESSINGS,
            aggregateMapper(Blessing::class),
            get()
        )
    }

    single<CharacterItemRepository<Miracle>>(named(Service.MIRACLE_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_MIRACLES,
            aggregateMapper(Miracle::class),
            get()
        )
    }

    viewModel { (characterId: CharacterId) ->
        BlessingsViewModel(characterId, get(named(Service.BLESSING_REPOSITORY)), blessingCompendium())
    }

    viewModel { (characterId: CharacterId) ->
        MiraclesViewModel(characterId, get(named(Service.MIRACLE_REPOSITORY)), miracleCompendium())
    }
}

private enum class Service {
    BLESSING_REPOSITORY,
    MIRACLE_REPOSITORY,
}