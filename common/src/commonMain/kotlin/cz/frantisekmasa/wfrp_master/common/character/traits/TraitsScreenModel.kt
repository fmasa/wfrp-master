package cz.frantisekmasa.wfrp_master.common.character.traits

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacterEffect
import cz.frantisekmasa.wfrp_master.common.character.effects.TraitEffectFactory
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

class TraitsScreenModel(
    private val characterId: CharacterId,
    private val traitRepository: TraitRepository,
    private val compendium: Compendium<CompendiumTrait>,
    private val characters: CharacterRepository,
    private val firestore: Firestore,
    private val traitEffectFactory: TraitEffectFactory,
) : CharacterItemScreenModel<Trait, CompendiumTrait>(characterId, traitRepository, compendium) {

    fun removeTrait(trait: Trait) = coroutineScope.launch(
        Dispatchers.IO
    ) {
        val allTraits = traitRepository.findAllForCharacter(characterId).first()
        val character = characters.get(characterId)

        val otherEffects = allTraits
            .filter { it.id != trait.id }
            .flatMap { traitEffectFactory.getEffects(it) }

        val updatedCharacter = character.revert(traitEffectFactory.getEffects(trait), otherEffects)

        traitRepository.remove(characterId, trait.id)

        firestore.runTransaction { transaction ->
            if (updatedCharacter != character) {
                characters.save(transaction, characterId.partyId, updatedCharacter)
            }
            traitRepository.remove(transaction, characterId, trait.id)
        }
    }

    suspend fun saveTrait(trait: Trait) {
        val allTraits = traitRepository.findAllForCharacter(characterId).first()
        val character = characters.get(characterId)

        val previousVersion = allTraits.firstOrNull { it.id == trait.id }
        val otherEffects = allTraits
            .filter { it != previousVersion }
            .flatMap { traitEffectFactory.getEffects(it) }

        var updatedCharacter = if (previousVersion != null)
            character.revert(traitEffectFactory.getEffects(previousVersion), otherEffects)
        else character

        val newEffects = traitEffectFactory.getEffects(trait)
        updatedCharacter = updatedCharacter.apply(newEffects, otherEffects)

        firestore.runTransaction { transaction ->
            if (updatedCharacter != character) {
                characters.save(transaction, characterId.partyId, updatedCharacter)
            }
            traitRepository.save(transaction, characterId, trait)
        }
    }

    private fun Character.apply(
        effects: List<CharacterEffect>,
        otherEffects: List<CharacterEffect>,
    ): Character {
        return effects.fold(this) { character, effect -> effect.apply(character, otherEffects) }
    }

    private fun Character.revert(
        effects: List<CharacterEffect>,
        otherEffects: List<CharacterEffect>,
    ): Character {
        return effects.fold(this) { character, effect -> effect.revert(character, otherEffects) }
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

        saveTrait(
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
