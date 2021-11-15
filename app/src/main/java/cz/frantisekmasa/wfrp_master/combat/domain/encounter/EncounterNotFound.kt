package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId

class EncounterNotFound(id: EncounterId, cause: Throwable?) :
    Exception("Encounter ${id.encounterId} was not found in party ${id.partyId}", cause)
