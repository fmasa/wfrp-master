package cz.frantisekmasa.wfrp_master.common.character.effects

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class EffectManager(
    private val characters: CharacterRepository,
    private val traits: TraitRepository,
    private val talents: TalentRepository,
    private val translatorFactory: Translator.Factory,
) {

    suspend fun <T> saveItem(
        transaction: Transaction,
        party: Party,
        characterId: CharacterId,
        repository: CharacterItemRepository<T>,
        item: T,
        previousItemVersion: T?,
    ): Unit where T : EffectSource, T : CharacterItem<T, *> = coroutineScope {
        val translator = translator(party)

        val newEffects = item.getEffects(translator)
        val previousEffects = previousItemVersion?.getEffects(translator) ?: emptyList()

        if (newEffects == previousEffects) {
            // Fast path, no need to load other effect sources and update character
            repository.save(transaction, characterId, item)
            return@coroutineScope
        }

        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }
        val effectSources = effects(characterId, translator)

        val otherEffects = effectSources
            .filter { it.sourceId != item.id }
            .flatMap { it.effects }
            .toList()

        val character = characterDeferred.await()
        val updatedCharacter = character
            .revert(previousEffects, otherEffects)
            .apply(newEffects, otherEffects)

        if (updatedCharacter != character) {
            characters.save(transaction, characterId.partyId, updatedCharacter)
        }

        repository.save(transaction, characterId, item)
    }

    suspend fun <T> removeItem(
        transaction: Transaction,
        party: Party,
        characterId: CharacterId,
        repository: CharacterItemRepository<T>,
        item: T,
    ): Unit where T : EffectSource, T : CharacterItem<T, *> = coroutineScope {
        val translator = translator(party)

        val characterDeferred = async(Dispatchers.IO) { characters.get(characterId) }
        val effectSources = effects(characterId, translator)

        val otherEffects = effectSources
            .filter { it.sourceId != item.id }
            .flatMap { it.effects }
            .toList()

        val character = characterDeferred.await()
        val updatedCharacter = character.revert(item.getEffects(translator), otherEffects)

        if (updatedCharacter != character) {
            characters.save(transaction, characterId.partyId, updatedCharacter)
        }

        repository.remove(transaction, characterId, item.id)
    }

    private suspend fun effects(
        characterId: CharacterId,
        translator: Translator,
    ): Sequence<Effects> {
        return coroutineScope {
            val traits = async(Dispatchers.IO) { traits.findAllForCharacter(characterId).first() }
            val talents = async(Dispatchers.IO) { talents.findAllForCharacter(characterId).first() }

            sequenceOf(traits.await(), talents.await())
                .flatten()
                .map { Effects(it.id, it.getEffects(translator)) }
        }
    }

    private data class Effects(
        val sourceId: Uuid,
        val effects: List<CharacterEffect>,
    )

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

    private fun translator(party: Party): Translator {
        return translatorFactory.create(party.settings.language)
    }
}
