package cz.frantisekmasa.wfrp_master.common.core.domain

class ExceptionWithUserMessage(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
