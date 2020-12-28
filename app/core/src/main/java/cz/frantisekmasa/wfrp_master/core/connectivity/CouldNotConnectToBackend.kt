package cz.frantisekmasa.wfrp_master.core.connectivity

import java.lang.Exception

class CouldNotConnectToBackend(cause: Throwable) : Exception(cause.message, cause)