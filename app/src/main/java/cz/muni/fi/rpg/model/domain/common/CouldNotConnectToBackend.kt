package cz.muni.fi.rpg.model.domain.common

import java.lang.Exception

class CouldNotConnectToBackend(cause: Throwable) : Exception(cause.message, cause)