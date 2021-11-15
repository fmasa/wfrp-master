package cz.frantisekmasa.wfrp_master.common.core.connectivity

class CouldNotConnectToBackend(cause: Throwable) : Exception(cause.message, cause)
