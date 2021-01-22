package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.EncounterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class EncountersViewModel(
    private val partyId: PartyId,
    private val encounterRepository: EncounterRepository
) : ViewModel() {

    val encounters: Flow<List<Encounter>> by lazy { encounterRepository.findByParty(partyId) }

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

        encounterRepository.save(partyId, encounter.update(name, description))
    }

    fun reorderEncounters(positions: Map<UUID, Int>) = viewModelScope.launch(Dispatchers.IO) {
        val encounters = positions.keys
            .map(::encounterAsync)
            .awaitAll()
            .toMap()

        val changedEncounters = positions.mapNotNull { (encounterId, newPosition) ->
            val encounter = encounters.getValue(encounterId)

            if (encounter.position != newPosition) encounter.changePosition(newPosition) else null
        }

        encounterRepository.save(partyId, *changedEncounters.toTypedArray())
    }

    private fun encounterAsync(id: UUID): Deferred<Pair<UUID, Encounter>> {
        return viewModelScope.async(Dispatchers.IO) {
            id to encounterRepository.get(
                EncounterId(
                    partyId = partyId,
                    encounterId = id
                )
            )
        }
    }
}