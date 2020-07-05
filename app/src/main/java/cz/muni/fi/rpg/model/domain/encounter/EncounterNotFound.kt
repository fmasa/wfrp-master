package cz.muni.fi.rpg.model.domain.encounter

import cz.muni.fi.rpg.model.domain.encounters.EncounterId

class EncounterNotFound(id: EncounterId, cause: Throwable?)
    : Exception("Encounter ${id.encounterId} was not found in party ${id.partyId}", cause)