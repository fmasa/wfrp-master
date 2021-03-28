package cz.frantisekmasa.wfrp_master.religion

import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ReligionModule = module {
    single(named(Service.BLESSING_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_BLESSINGS,
            aggregateMapper(Blessing::class),
            get()
        )
    }

    single(named(Service.MIRACLE_REPOSITORY)) {
        FirestoreCharacterItemRepository(
            COLLECTION_MIRACLES,
            aggregateMapper(Miracle::class),
            get()
        )
    }
}

private enum class Service {
    BLESSING_REPOSITORY,
    MIRACLE_REPOSITORY,
}