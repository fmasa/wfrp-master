package cz.frantisekmasa.wfrp_master.core.domain.party

import java.lang.Exception

class PartyNotFound(partyId: PartyId, cause: Throwable?) :
    Exception("Party $partyId was not found or user does not have access to it", cause)
