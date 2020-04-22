package cz.muni.fi.rpg.model.domain.invitation

import java.lang.Exception

class InvalidInvitation(message: String, cause: Throwable?) : Exception(message, cause)
