package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.encounter.*
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.*

class EncounterDetailViewModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val combatantRepository: CombatantRepository,
    parties: PartyRepository
) : ViewModel() {

    val party: LiveData<Either<PartyNotFound, Party>> = parties.getLive(encounterId.partyId)
    val encounter: LiveData<Either<EncounterNotFound, Encounter>> = encounters.getLive(encounterId)
    val combatants: LiveData<List<Combatant>> = combatantRepository.findByEncounter(encounterId)

    suspend fun remove() {
        encounters.remove(encounterId)
    }

    suspend fun addCombatant(
        name: String,
        note: String,
        wounds: Wounds,
        stats: Stats,
        armor: Armor,
        enemy: Boolean,
        traits: List<String>,
        trappings: List<String>
    ) {
        combatantRepository.save(
            encounterId,
            Combatant(
                UUID.randomUUID(),
                name = name,
                note = note,
                wounds = wounds,
                stats = stats,
                armor = armor,
                enemy = enemy,
                traits = traits,
                trappings = trappings,
                position = combatantRepository.getNextPosition(encounterId)
            )
        )
    }

    suspend fun removeCombatant(combatantId: UUID) {
        combatantRepository.remove(CombatantId(encounterId, combatantId))
    }
}