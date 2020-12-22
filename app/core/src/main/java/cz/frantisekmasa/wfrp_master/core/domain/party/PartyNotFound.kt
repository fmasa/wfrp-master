package cz.frantisekmasa.wfrp_master.core.domain.party

import java.lang.Exception
import java.util.*

class PartyNotFound(partyId: UUID, cause: Throwable?)
    : Exception("Party $partyId was not found or user does not have access to it", cause)