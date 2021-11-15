package cz.frantisekmasa.wfrp_master.common.core.logging

expect object Reporter {
    fun setUserId(id: String)

    fun log(message: String)

    fun recordThrowable(throwable: Throwable)
}