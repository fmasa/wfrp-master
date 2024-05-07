package cz.frantisekmasa.wfrp_master.common.character.spells.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell as CompendiumSpell

class AddSpellScreenModel(
    private val characterId: CharacterId,
    private val spells: SpellRepository,
    compendium: Compendium<CompendiumSpell>,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state =
        availableCompendiumItemsFactory.create(
            characterId.partyId,
            compendium = compendium,
            filterCharacterItems = spells.findAllForCharacter(characterId),
        ).map {
            AddSpellScreenState(
                availableCompendiumItems = it,
            )
        }

    suspend fun saveItem(spell: Spell) {
        spells.save(characterId, spell)
    }
}
