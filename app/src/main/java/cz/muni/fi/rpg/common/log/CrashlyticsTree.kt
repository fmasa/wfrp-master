package cz.muni.fi.rpg.common.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Handles log entries as either log context or error reporting to Crashlytics
 */
class CrashlyticsTree(
    private val crashlytics: FirebaseCrashlytics
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (priority == Log.WARN || priority == Log.ERROR) {
            crashlytics.recordException(t ?: Exception(message))
        }
    }
}