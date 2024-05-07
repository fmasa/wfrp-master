package cz.frantisekmasa.wfrp_master.common.core.cache

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlin.jvm.Synchronized

internal class PartyRepositoryIdentityMap(
    maxEntries: Int,
    private val inner: PartyRepository,
) : PartyRepository by inner {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val identityMap = IdentityMap<PartyId, Flow<Either<PartyNotFound, Party>>>(maxEntries)

    @Synchronized
    override fun getLive(id: PartyId) =
        identityMap.getOrPut(id) {
            inner.getLive(id).shareIn(scope, SharingStarted.WhileSubscribed(), 1)
        }
}
