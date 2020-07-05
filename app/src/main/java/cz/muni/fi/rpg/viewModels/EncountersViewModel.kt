package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.coroutines.*
import java.util.*

class EncountersViewModel(
    private val partyId: UUID,
    private val encounterRepository: EncounterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val encounters: LiveData<List<Encounter>> by lazy { encounterRepository.findByParty(partyId) }

    suspend fun createEncounter(name: String, description: String) {
        encounterRepository.save(
            partyId,
            Encounter(
                UUID.randomUUID(),
                name,
                description,
                encounterRepository.getNextPosition(partyId)
            )
        )
    }

    suspend fun updateEncounter(id: UUID, name: String, description: String) {
        val encounter = encounterRepository.get(EncounterId(partyId = partyId, encounterId = id))

        encounter.update(name, description)

        encounterRepository.save(partyId, encounter)
    }

    suspend fun reorderEncounters(positions: Map<UUID, Int>) {
        val changedEncounters = mutableListOf<Encounter>()

        val encounters = mapOf(
            *awaitAll(
                *positions.keys.map(this::encounterAsync).toTypedArray()
            ).toTypedArray()
        )

        positions.forEach { (encounterId, newPosition) ->
            encounters[encounterId]?.let { encounter ->
                if (encounter.position != newPosition) {
                    encounter.position = newPosition
                    changedEncounters.add(encounter)
                }
            }
        }

        encounterRepository.save(partyId, *changedEncounters.toTypedArray())
    }

    private fun encounterAsync(id: UUID): Deferred<Pair<UUID, Encounter>> {
        return async {
            id to encounterRepository.get(
                EncounterId(
                    partyId = partyId,
                    encounterId = id
                )
            )
        }
    }
}