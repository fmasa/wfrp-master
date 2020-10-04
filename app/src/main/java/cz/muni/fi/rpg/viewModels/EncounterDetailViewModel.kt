package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.encounter.*
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import kotlin.math.min

class EncounterDetailViewModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val npcRepository: NpcRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val encounter: LiveData<Either<EncounterNotFound, Encounter>> = encounters.getLive(encounterId)
    val npcs: LiveData<List<Npc>> = npcRepository.findByEncounter(encounterId)

    suspend fun remove() {
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

    suspend fun addNpc(
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

    @ExperimentalCoroutinesApi
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

    fun removeCombatant(npcId: UUID) = launch {
        npcRepository.remove(NpcId(encounterId, npcId))
    }
}