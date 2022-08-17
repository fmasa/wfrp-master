package cz.frantisekmasa.wfrp_master.common.encounters

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.utils.mapItems
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Npc
import cz.frantisekmasa.wfrp_master.common.encounters.domain.NpcRepository
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.min


class EncounterDetailScreenModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val npcRepository: NpcRepository,
    private val characters: CharacterRepository,
    private val parties: PartyRepository,
) : ScreenModel {
    val encounter: Flow<Encounter> = encounters.getLive(encounterId).right()
    val npcs: Flow<List<NpcListItem>> = npcRepository
        .findByEncounter(encounterId)
        .mapItems { NpcListItem(NpcId(encounterId, it.id), it.name, it.alive) }
        .distinctUntilChanged()
    val allNpcsCharacters: Flow<List<Character>> =
        characters.inParty(encounterId.partyId, CharacterType.NPC)

    suspend fun remove() {
        parties.update(encounterId.partyId) { party ->
            if (party.activeCombat?.encounterId == encounterId.encounterId)
                party.endCombat()
            else party
        }

        encounters.remove(encounterId)
    }

    suspend fun updateEncounter(encounter: Encounter) {
        encounters.save(encounterId.partyId, encounter)
    }

    suspend fun addNpc(
        name: String,
        note: String,
        wounds: Wounds,
        stats: Stats,
        armor: Armour,
        enemy: Boolean,
        alive: Boolean,
        traits: List<String>,
        trappings: List<String>
    ) {
        npcRepository.save(
            encounterId,
            Npc(
                UUID.randomUUID(),
                name = name,
                note = note,
                wounds = wounds,
                stats = stats,
                armor = armor,
                enemy = enemy,
                alive = alive,
                traits = traits,
                trappings = trappings,
                position = npcRepository.getNextPosition(encounterId)
            )
        )
    }

    suspend fun duplicateNpc(id: UUID) {
        val npc = npcRepository.get(NpcId(encounterId, id))

        npcRepository.save(
            encounterId,
            npc.duplicate(position = npcRepository.getNextPosition(encounterId))
        )
    }

    suspend fun updateNpc(
        id: UUID,
        name: String,
        note: String,
        maxWounds: Int,
        stats: Stats,
        armor: Armour,
        enemy: Boolean,
        alive: Boolean,
        traits: List<String>,
        trappings: List<String>
    ) {
        val npc = npcRepository.get(NpcId(encounterId, id))

        npcRepository.save(
            encounterId,
            npc.copy(
                name = name,
                note = note,
                wounds = Wounds(min(npc.wounds.current, maxWounds), maxWounds),
                stats = stats,
                armor = armor,
                enemy = enemy,
                alive = alive,
                traits = traits,
                trappings = trappings,
            )
        )
    }

    fun npcFlow(npcId: NpcId): StateFlow<Npc?> {
        val flow = MutableStateFlow<Npc?>(null)

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val npc = npcRepository.get(npcId)
                flow.value = npc
            } catch (e: Throwable) {
                Napier.e(e.toString(), e)
                throw e
            }
        }

        return flow
    }

    fun removeNpc(npcId: NpcId) = coroutineScope.launch(Dispatchers.IO) {
        parties.update(encounterId.partyId) { party ->
            val combat = party.activeCombat
                ?: return@update party // Skip update

            when (val updatedCombat = combat.removeNpc(npcId)) {
                null -> party.endCombat()
                combat -> party
                else -> party.updateCombat(updatedCombat)
            }
        }

        npcRepository.remove(npcId)
    }
}
