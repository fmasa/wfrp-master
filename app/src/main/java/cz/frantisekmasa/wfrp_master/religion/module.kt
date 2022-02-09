package cz.frantisekmasa.wfrp_master.religion

import cz.frantisekmasa.wfrp_master.compendium.infrastructure.FirestoreCompendium
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.firestore.serialization.serializationAggregateMapper
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.frantisekmasa.wfrp_master.religion.ui.blessings.BlessingsViewModel
import cz.frantisekmasa.wfrp_master.religion.ui.miracles.MiraclesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing as CompendiumBlessing
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle as CompendiumMiracle

val ReligionModule = module {

    fun Scope.blessingCompendium() = FirestoreCompendium<CompendiumBlessing>(
        COLLECTION_COMPENDIUM_BLESSINGS,
        get(),
        serializationAggregateMapper(),
    )

    fun Scope.miracleCompendium() = FirestoreCompendium<CompendiumMiracle>(
        COLLECTION_COMPENDIUM_MIRACLES,
        get(),
        serializationAggregateMapper(),
    )

    single<CharacterItemRepository<Blessing>>(named(Service.BLESSING_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_BLESSINGS,
            serializationAggregateMapper(),
            get()
        )
    }

    single<CharacterItemRepository<Miracle>>(named(Service.MIRACLE_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_MIRACLES,
            serializationAggregateMapper(),
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
