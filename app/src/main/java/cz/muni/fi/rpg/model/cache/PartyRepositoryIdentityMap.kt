package cz.muni.fi.rpg.model.cache

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.UUID

internal class PartyRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: PartyRepository
) : PartyRepository by inner {

    private val identityMap = IdentityMap<UUID, LiveData<Either<PartyNotFound, Party>>>(maxEntries)

    @Synchronized
    override fun getLive(id: UUID): LiveData<Either<PartyNotFound, Party>> {
        return identityMap.getOrPut(id, { inner.getLive(id) })
    }
}