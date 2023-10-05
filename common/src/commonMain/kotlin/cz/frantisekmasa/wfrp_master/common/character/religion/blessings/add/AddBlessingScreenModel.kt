package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing as CompendiumBlessing

class AddBlessingScreenModel(
    private val characterId: CharacterId,
    private val blessings: CharacterItemRepository<Blessing>,
    compendium: Compendium<CompendiumBlessing>,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {

    val state = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        filterCharacterItems = blessings.findAllForCharacter(characterId),
    ).map { compendiumItemChooserState ->
        AddBlessingScreenState(
            availableCompendiumItems = compendiumItemChooserState,
        )
    }

    suspend fun addBlessing(blessing: Blessing) {
        blessings.save(characterId, blessing)
    }
}
