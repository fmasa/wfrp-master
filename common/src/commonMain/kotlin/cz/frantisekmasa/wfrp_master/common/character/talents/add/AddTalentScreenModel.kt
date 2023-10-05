package cz.frantisekmasa.wfrp_master.common.character.talents.add

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TimesTakenForm
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent as CompendiumTalent

class AddTalentScreenModel(
    private val characterId: CharacterId,
    private val talents: CharacterItemRepository<Talent>,
    private val compendium: Compendium<CompendiumTalent>,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    private val parties: PartyRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {

    val state = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        filterCharacterItems = talents.findAllForCharacter(characterId),
    ).map { compendiumItemChooserState ->
        AddTalentScreenState(
            availableCompendiumItems = compendiumItemChooserState,
        )
    }

    suspend fun addCompendiumTalent(
        compendiumTalentId: Uuid,
        timesTaken: Int,
    ): TimesTakenForm.SavingResult {
        val compendiumTalent = try {
            compendium.getItem(
                partyId = characterId.partyId,
                itemId = compendiumTalentId,
            )
        } catch (e: CompendiumItemNotFound) {
            return TimesTakenForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED
        }

        addTalent(
            Talent(
                id = uuid4(),
                compendiumId = compendiumTalent.id,
                name = compendiumTalent.name,
                tests = compendiumTalent.tests,
                description = compendiumTalent.description,
                taken = timesTaken,
            )
        )

        return TimesTakenForm.SavingResult.SUCCESS
    }

    suspend fun addTalent(talent: Talent) {
        firestore.runTransaction { transaction ->
            effectManager.saveItem(
                transaction,
                parties.get(characterId.partyId),
                characterId,
                talents,
                item = talent,
                previousItemVersion = null,
            )
        }
    }
}
