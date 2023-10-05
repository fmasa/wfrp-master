package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle as CompendiumMiracle

class AddMiracleScreenModel(
    private val characterId: CharacterId,
    private val miracles: CharacterItemRepository<Miracle>,
    compendium: Compendium<CompendiumMiracle>,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {

    val state = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        filterCharacterItems = miracles.findAllForCharacter(characterId)
    ).map { compendiumItemChooserState ->
        AddMiracleScreenState(
            availableCompendiumItems = compendiumItemChooserState,
        )
    }

    suspend fun addMiracle(miracle: Miracle) {
        miracles.save(characterId, miracle)
    }
}
