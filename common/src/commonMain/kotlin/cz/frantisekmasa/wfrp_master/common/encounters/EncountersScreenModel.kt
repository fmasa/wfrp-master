package cz.frantisekmasa.wfrp_master.common.encounters

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class EncountersScreenModel(
    private val partyId: PartyId,
    private val encounterRepository: EncounterRepository
) : ScreenModel {

    private val encounters: Flow<List<Encounter>> = encounterRepository.findByParty(partyId)

    val notCompletedEncounters: Flow<List<Encounter>> =
        encounters.map { items -> items.filter { !it.completed } }
            .distinctUntilChanged()

    val allEncounters: Flow<List<Encounter>> =
        encounters.distinctUntilChanged()
            .map { items -> items.sortedBy { it.name } }

    suspend fun createEncounter(name: String, description: String) {
        val encounterId = UUID.randomUUID()

        encounterRepository.save(
            partyId,
            Encounter(
                encounterId,
                name,
                description,
                encounterRepository.getNextPosition(partyId)
            )
        )

        Reporter.recordEvent(
            "create_encounter",
            mapOf(
                "encounterId" to encounterId.toString(),
                "partyId" to partyId.toString(),
            )
        )
    }

    suspend fun updateEncounter(id: UUID, name: String, description: String) {
        val encounter = encounterRepository.get(EncounterId(partyId = partyId, encounterId = id))

        encounterRepository.save(partyId, encounter.update(name, description))
    }

    fun reorderEncounters(positions: Map<UUID, Int>) = coroutineScope.launch(Dispatchers.IO) {
        val encounters = positions.keys
            // This is terribly non-optimal
            // TODO: Load all encounters at once
            // and/or use lexographical ordering
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
        return coroutineScope.async(Dispatchers.IO) {
            id to encounterRepository.get(
                EncounterId(
                    partyId = partyId,
                    encounterId = id
                )
            )
        }
    }
}
