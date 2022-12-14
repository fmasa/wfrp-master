package cz.frantisekmasa.wfrp_master.common.encounters.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId

class EncounterNotFound(id: EncounterId, cause: Throwable? = null) :
    Exception("Encounter ${id.encounterId} was not found in party ${id.partyId}", cause)
