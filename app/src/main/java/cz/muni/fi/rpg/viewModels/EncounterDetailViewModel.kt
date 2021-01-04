package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.*
import cz.frantisekmasa.wfrp_master.core.domain.Armor
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.mapItems
import cz.frantisekmasa.wfrp_master.core.utils.right
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcListItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import kotlin.math.min

class EncounterDetailViewModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val npcRepository: NpcRepository,
    private val parties: PartyRepository,
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val encounter: Flow<Encounter> = encounters.getLive(encounterId).right()
    val npcs: Flow<List<NpcListItem>> = npcRepository
        .findByEncounter(encounterId)
        .mapItems { NpcListItem(NpcId(encounterId, it.id), it.name, it.alive) }
        .distinctUntilChanged()

    suspend fun remove() {
        val party = parties.get(encounterId.partyId)

        if (party.getActiveCombat()?.encounterId == encounterId.encounterId) {
            party.endCombat()
            parties.save(party)
        }

        encounters.remove(encounterId)
    }

    suspend fun addNpc(
        name: String,
        note: String,
        wounds: Wounds,
        stats: Stats,
        armor: Armor,
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

    suspend fun updateNpc(
        id: UUID,
        name: String,
        note: String,
        maxWounds: Int,
        stats: Stats,
        armor: Armor,
        enemy: Boolean,
        alive: Boolean,
        traits: List<String>,
        trappings: List<String>
    ) {
        val npc = npcRepository.get(NpcId(encounterId, id))

        npc.update(
            name,
            note,
            Wounds(min(npc.wounds.current, maxWounds), maxWounds),
            stats,
            armor,
            enemy,
            alive,
            traits,
            trappings
        )

        npcRepository.save(encounterId, npc)
    }

    fun npcFlow(npcId: NpcId): StateFlow<Npc?> {
        val flow = MutableStateFlow<Npc?>(null)

        launch {
            try {
                val npc = npcRepository.get(npcId)
                withContext(Dispatchers.Main) { flow.value = npc }
            } catch (e: Throwable) {
                Timber.e(e)
                throw e
            }
        }

        return flow
    }

    fun removeNpc(npcId: NpcId) = launch { npcRepository.remove(npcId) }
}