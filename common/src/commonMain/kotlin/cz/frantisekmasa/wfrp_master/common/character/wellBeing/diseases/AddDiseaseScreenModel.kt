package cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease as CompendiumDisease

class AddDiseaseScreenModel(
    private val characterId: CharacterId,
    private val diseases: CharacterItemRepository<Disease>,
    compendium: Compendium<CompendiumDisease>,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state =
        availableCompendiumItemsFactory.create(
            partyId = characterId.partyId,
            compendium = compendium,
            filterCharacterItems =
                diseases.findAllForCharacter(characterId)
                    .map { diseases -> diseases.filter { !it.isHealed } },
        ).map { compendiumItemChooserState ->
            AddDiseaseScreenState(
                availableCompendiumItems = compendiumItemChooserState,
            )
        }

    suspend fun addDisease(disease: Disease) {
        diseases.save(characterId, disease)
    }
}
