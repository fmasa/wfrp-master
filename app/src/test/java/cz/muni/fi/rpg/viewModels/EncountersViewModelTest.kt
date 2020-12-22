package cz.muni.fi.rpg.viewModels

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.EncounterNotFound
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import kotlinx.coroutines.flow.Flow
import org.junit.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*

class EncountersViewModelTest {
    private class Repository(val positions: MutableMap<UUID, Int>) : EncounterRepository {
        override suspend fun get(id: EncounterId): Encounter {
            return positions[id.encounterId]?.let {
                Encounter(id.encounterId, "x", "", it)
            } ?: throw EncounterNotFound(id, null)
        }

        override fun getLive(id: EncounterId): Flow<Either<EncounterNotFound, Encounter>> {
            error("Not necessary")
        }

        override suspend fun save(partyId: UUID, vararg encounters: Encounter) {
            encounters.forEach {
                positions[it.id] = it.position
            }
        }

        override fun findByParty(partyId: UUID): Flow<List<Encounter>> = error("Not necessary")
        override suspend fun remove(id: EncounterId) = error("Not necessary")
        override suspend fun getNextPosition(partyId: UUID): Int = error("Not necessary")
    }

    @Test
    fun testReorderingOfEncounters() {
        val encounters = mapOf(
            UUID.fromString("1de4c847-068d-4e3f-9138-1e607b011101") to 0,
            UUID.fromString("26d1a986-582d-4f44-a6f7-8d559d5199b6") to 1,
            UUID.fromString("6fef50a9-3087-44cd-b965-7c786ec02986") to 2,
            UUID.fromString("593661a4-f479-4841-842a-288a50d5d6db") to 3
        )

        val repository = Repository(mutableMapOf(*encounters.toList().toTypedArray()))
        val viewModel = EncountersViewModel(UUID.randomUUID(), repository)

        runBlocking {
            viewModel.reorderEncounters(
                mapOf(
                    UUID.fromString("1de4c847-068d-4e3f-9138-1e607b011101") to 2,
                    UUID.fromString("26d1a986-582d-4f44-a6f7-8d559d5199b6") to 0,
                    UUID.fromString("6fef50a9-3087-44cd-b965-7c786ec02986") to 3,
                    UUID.fromString("593661a4-f479-4841-842a-288a50d5d6db") to 1
                )
            ).join()
        }

        assertEquals(
            listOf(
                "26d1a986-582d-4f44-a6f7-8d559d5199b6",
                "593661a4-f479-4841-842a-288a50d5d6db",
                "1de4c847-068d-4e3f-9138-1e607b011101",
                "6fef50a9-3087-44cd-b965-7c786ec02986"
            ).map(UUID::fromString),
            repository.positions.toList().sortedBy { it.second }.map { it.first }
        )
    }
}