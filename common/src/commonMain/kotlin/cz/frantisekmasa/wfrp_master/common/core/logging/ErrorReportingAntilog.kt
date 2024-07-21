package cz.frantisekmasa.wfrp_master.common.core.logging

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class ErrorReportingAntilog : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?,
    ) {
        message?.let { Reporting.log(it) }

        if (priority == LogLevel.WARNING || priority == LogLevel.ERROR) {
            when {
                throwable != null -> Reporting.recordThrowable(throwable)
                message != null -> Reporting.recordThrowable(Exception(message))
            }
        }
    }
}
