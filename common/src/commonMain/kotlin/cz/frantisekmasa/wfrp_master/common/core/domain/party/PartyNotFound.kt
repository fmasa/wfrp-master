package cz.frantisekmasa.wfrp_master.common.core.domain.party

class PartyNotFound(partyId: PartyId, cause: Throwable? = null) :
    Exception("Party $partyId was not found or user does not have access to it", cause)
