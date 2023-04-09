package cz.frantisekmasa.wfrp_master.common.npcs

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NpcsScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
    private val skills: SkillRepository,
    private val talents: TalentRepository,
    private val traits: TraitRepository,
    private val spells: SpellRepository,
    private val blessings: BlessingRepository,
    private val miracles: MiracleRepository,
    private val trappings: InventoryItemRepository,
    private val firestore: Firestore,
) : ScreenModel {

    val npcs: Flow<List<Character>> = characters.inParty(partyId, CharacterType.NPC)

    suspend fun archiveNpc(npc: Character) {
        characters.save(partyId, npc.archive())
    }

    suspend fun duplicate(npc: Character) {
        firestore.runTransaction { transaction ->
            val newNpc = npc.duplicate()
            val existingCharacterId = CharacterId(partyId, npc.id)
            val newCharacterId = CharacterId(partyId, newNpc.id)

            copyItems(transaction, skills, existingCharacterId, newCharacterId)
            copyItems(transaction, talents, existingCharacterId, newCharacterId)
            copyItems(transaction, traits, existingCharacterId, newCharacterId)
            copyItems(transaction, spells, existingCharacterId, newCharacterId)
            copyItems(transaction, blessings, existingCharacterId, newCharacterId)
            copyItems(transaction, miracles, existingCharacterId, newCharacterId)
            copyItems(transaction, trappings, existingCharacterId, newCharacterId)

            characters.save(transaction, partyId, newNpc)
        }
    }

    private suspend fun <T : CharacterItem<T, *>> copyItems(
        transaction: Transaction,
        repository: CharacterItemRepository<T>,
        existingCharacterId: CharacterId,
        newCharacterId: CharacterId,
    ) {
        repository.findAllForCharacter(existingCharacterId)
            .first()
            .forEach { item ->
                repository.save(transaction, newCharacterId, item)
            }
    }
}
