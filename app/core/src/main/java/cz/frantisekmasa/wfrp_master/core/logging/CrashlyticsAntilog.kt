package cz.frantisekmasa.wfrp_master.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

/**
 * Handles log entries as either log context or error reporting to Crashlytics
 */
class CrashlyticsAntilog(
    private val crashlytics: FirebaseCrashlytics,
) : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        message?.let { crashlytics.log(it) }

        if (priority == LogLevel.WARNING || priority == LogLevel.ERROR) {
            when {
                throwable != null -> crashlytics.recordException(throwable)
                message != null -> crashlytics.recordException(Exception(message))
            }
        }
    }
}