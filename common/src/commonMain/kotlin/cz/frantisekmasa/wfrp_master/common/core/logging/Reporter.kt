package cz.frantisekmasa.wfrp_master.common.core.logging

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId

expect object Reporter {
    fun setUserId(id: UserId)

    fun log(message: String)

    fun recordThrowable(throwable: Throwable)

    fun recordEvent(
        name: String,
        parameters: Map<String, String>,
    )
}

/**
 * This object is added, because expect/actual classes are not autocompleted in the IDE.
 */
object Reporting {
    fun setUserId(id: UserId) = Reporter.setUserId(id)

    fun recordEvent(
        name: String,
        parameters: Map<String, String>,
    ) = Reporter.recordEvent(name, parameters)

    fun recordThrowable(throwable: Throwable) = Reporter.recordThrowable(throwable)

    fun log(message: String) = Reporter.log(message)
}
