package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import java.util.*

class EncountersViewModel(
    private val partyId: UUID,
    private val encounterRepository: EncounterRepository
) : ViewModel() {
    val encounters: LiveData<List<Encounter>> = encounterRepository.findByParty(partyId)
}