package cz.frantisekmasa.wfrp_master.common.character.traits

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

class TraitsScreenModel(
    private val characterId: CharacterId,
    private val traitRepository: TraitRepository,
    private val compendium: Compendium<CompendiumTrait>,
) : CharacterItemScreenModel<Trait, CompendiumTrait>(characterId, traitRepository, compendium) {

    fun removeTrait(Trait: Trait) = coroutineScope.launch(
        Dispatchers.IO) {
        traitRepository.remove(characterId, Trait.id)
    }

    suspend fun saveTrait(trait: Trait) {
        traitRepository.save(characterId, trait)
    }

    suspend fun saveCompendiumTrait(
        traitId: Uuid,
        compendiumTraitId: Uuid,
        specificationValues: Map<String, String>,
    ) {
        val compendiumTrait = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumTraitId,
        )

        traitRepository.save(
            characterId,
            Trait(
                id = traitId,
                compendiumId = compendiumTrait.id,
                name = compendiumTrait.name,
                description = compendiumTrait.description,
                specificationValues = specificationValues.toMap(),
            )
        )
    }
}
