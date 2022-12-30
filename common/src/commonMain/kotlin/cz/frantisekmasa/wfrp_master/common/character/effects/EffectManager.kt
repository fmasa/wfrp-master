package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class EffectManager(
    private val characters: CharacterRepository,
    private val traits: TraitRepository,
    private val talents: TalentRepository,
) {

    suspend fun saveEffectSource(
        transaction: Transaction,
        characterId: CharacterId,
        source: EffectSource,
    ): Unit = coroutineScope {
        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }

        val effectSources = effectSources(characterId)
        val previousSourceVersion = effectSources.firstOrNull { it.id == source.id }

        if (source.effects == (previousSourceVersion?.effects ?: emptyList<CharacterEffect>())) {
            // Fast path, no need to load other effect sources and update character
            saveSource(transaction, characterId, source)
            return@coroutineScope
        }

        val otherEffects = effectSources
            .filter { it.id != source.id }
            .flatMap { it.effects }

        val character = characterDeferred.await()
        var updatedCharacter = if (previousSourceVersion != null)
            character.revert(previousSourceVersion.effects, otherEffects)
        else character

        val newEffects = source.effects
        updatedCharacter = updatedCharacter.apply(newEffects, otherEffects)

        if (updatedCharacter != character) {
            characters.save(transaction, characterId.partyId, updatedCharacter)
        }

        saveSource(transaction, characterId, source)
    }

    private fun saveSource(
        transaction: Transaction,
        characterId: CharacterId,
        source: EffectSource,
    ) {
        when (source) {
            is EffectSource.Trait -> {
                traits.save(transaction, characterId, source.trait)
            }
            is EffectSource.Talent -> {
                talents.save(transaction, characterId, source.talent)
            }
        }
    }

    suspend fun removeEffectSource(
        transaction: Transaction,
        characterId: CharacterId,
        source: EffectSource,
    ): Unit = coroutineScope {
        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }

        val effectSources = effectSources(characterId)
        val otherEffects = effectSources
            .filter { it.id != source.id }
            .flatMap { it.effects }

        val character = characterDeferred.await()
        val updatedCharacter = character.revert(source.effects, otherEffects)

        if (updatedCharacter != character) {
            characters.save(transaction, characterId.partyId, updatedCharacter)
        }

        when (source) {
            is EffectSource.Trait -> {
                traits.remove(transaction, characterId, source.trait.id)
            }
            is EffectSource.Talent -> {
                talents.remove(transaction, characterId, source.talent.id)
            }
        }
    }

    private suspend fun effectSources(characterId: CharacterId): List<EffectSource> = coroutineScope {
        val traits = async(Dispatchers.IO) { traits.findAllForCharacter(characterId).first() }
        val talents = async(Dispatchers.IO) { talents.findAllForCharacter(characterId).first() }

        traits.await().map { EffectSource.Trait(it) } +
            talents.await().map { EffectSource.Talent(it) }
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
