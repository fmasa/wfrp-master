package cz.frantisekmasa.wfrp_master.common.core.logging

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId

expect object Reporter {
    fun setUserId(id: UserId)

    fun log(message: String)

    fun recordThrowable(throwable: Throwable)

    fun recordEvent(name: String, parameters: Map<String, String>)
}
