package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class EffectManager(
    private val characters: CharacterRepository,
    private val traits: TraitRepository,
    private val effectFactory: EffectFactory,
    private val firestore: Firestore,
) {

    suspend fun saveEffectSource(
        characterId: CharacterId,
        source: EffectSource,
    ): Unit = coroutineScope {
        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }

        val effectSources = effectSources(characterId)
        val previousSourceVersion = effectSources.firstOrNull { it.id == source.id }
        val otherEffects = effectSources
            .filter { it.id != source.id }
            .flatMap { effectFactory.getEffects(it) }

        val character = characterDeferred.await()
        var updatedCharacter = if (previousSourceVersion != null)
            character.revert(effectFactory.getEffects(previousSourceVersion), otherEffects)
        else character

        val newEffects = effectFactory.getEffects(source)
        updatedCharacter = updatedCharacter.apply(newEffects, otherEffects)

        firestore.runTransaction { transaction ->
            if (updatedCharacter != character) {
                characters.save(transaction, characterId.partyId, updatedCharacter)
            }

            when (source) {
                is EffectSource.Trait -> {
                    traits.save(transaction, characterId, source.trait)
                }
            }
        }
    }

    suspend fun removeEffectSource(
        characterId: CharacterId,
        source: EffectSource,
    ): Unit = coroutineScope {
        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }

        val effectSources = effectSources(characterId)
        val otherEffects = effectSources
            .filter { it.id != source.id }
            .flatMap { effectFactory.getEffects(it) }

        val character = characterDeferred.await()
        val updatedCharacter = character.revert(effectFactory.getEffects(source), otherEffects)

        firestore.runTransaction { transaction ->
            if (updatedCharacter != character) {
                characters.save(transaction, characterId.partyId, updatedCharacter)
            }

            when (source) {
                is EffectSource.Trait -> {
                    traits.remove(transaction, characterId, source.trait.id)
                }
            }
        }
    }

    private suspend fun effectSources(characterId: CharacterId): List<EffectSource> {
        return traits.findAllForCharacter(characterId).first()
            .map { EffectSource.Trait(it) }
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

}