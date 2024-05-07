package cz.frantisekmasa.wfrp_master.common.character.items

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AvailableCompendiumItemsFactory(
    private val parties: PartyRepository,
    private val userProvider: UserProvider,
) : ScreenModel {
    fun <A : CharacterItem<A, B>, B : CompendiumItem<B>> create(
        partyId: PartyId,
        compendium: Compendium<B>,
        filterCharacterItems: Flow<List<A>>,
    ): Flow<AvailableCompendiumItems<B>> {
        return compendium.liveForParty(partyId)
            .combine(
                parties.getLive(partyId)
                    .right()
                    .map { it.gameMasterId }
                    .distinctUntilChanged(),
            ) { items, gameMasterId ->
                val filteredItems =
                    if (gameMasterId == null || gameMasterId == userProvider.userId) {
                        items
                    } else {
                        items.filter { it.isVisibleToPlayers }
                    }

                AvailableCompendiumItems(
                    availableCompendiumItems = filteredItems.toImmutableList(),
                    isCompendiumEmpty = filteredItems.isNotEmpty(),
                )
            }.combine(filterCharacterItems) { state, existing ->
                val existingCompendiumIds =
                    existing
                        .asSequence()
                        .mapNotNull { it.compendiumId }
                        .toSet()

                state.copy(
                    availableCompendiumItems =
                        state.availableCompendiumItems
                            .asSequence()
                            .filter { it.id !in existingCompendiumIds }
                            .toImmutableList(),
                )
            }
    }
}
