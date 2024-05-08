package cz.frantisekmasa.wfrp_master.common.character.talents.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent as CompendiumTalent

class AddTalentScreenModel(
    private val characterId: CharacterId,
    private val talents: CharacterItemRepository<Talent>,
    private val compendium: Compendium<CompendiumTalent>,
    private val effectManager: EffectManager,
    private val firestore: FirebaseFirestore,
    private val parties: PartyRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state =
        availableCompendiumItemsFactory.create(
            partyId = characterId.partyId,
            compendium = compendium,
            filterCharacterItems = talents.findAllForCharacter(characterId),
        ).map { compendiumItemChooserState ->
            AddTalentScreenState(
                availableCompendiumItems = compendiumItemChooserState,
            )
        }

    suspend fun addTalent(talent: Talent) {
        firestore.runTransaction {
            effectManager.saveItem(
                this,
                parties.get(this, characterId.partyId),
                characterId,
                talents,
                item = talent,
                previousItemVersion = null,
            )
        }
    }
}
