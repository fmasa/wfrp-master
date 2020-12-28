package cz.muni.fi.rpg.model.cache

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

internal class PartyRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: PartyRepository
) : PartyRepository by inner {

    private val identityMap = IdentityMap<UUID, Flow<Either<PartyNotFound, Party>>>(maxEntries)

    @Synchronized
    override fun getLive(id: UUID) = identityMap.getOrPut(id, { inner.getLive(id) })
}