package cz.muni.fi.rpg.model.cache

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import kotlinx.coroutines.flow.Flow

internal class PartyRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: PartyRepository
) : PartyRepository by inner {

    private val identityMap = IdentityMap<PartyId, Flow<Either<PartyNotFound, Party>>>(maxEntries)

    @Synchronized
    override fun getLive(id: PartyId) = identityMap.getOrPut(id, { inner.getLive(id) })
}