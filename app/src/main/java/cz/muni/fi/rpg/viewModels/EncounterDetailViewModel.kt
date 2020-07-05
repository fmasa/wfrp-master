package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.EncounterNotFound
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository

class EncounterDetailViewModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    parties: PartyRepository
) : ViewModel() {

    val party: LiveData<Either<PartyNotFound, Party>> = parties.getLive(encounterId.partyId)
    val encounter: LiveData<Either<EncounterNotFound, Encounter>> = encounters.getLive(encounterId)

    suspend fun remove() {
        encounters.remove(encounterId)
    }
}