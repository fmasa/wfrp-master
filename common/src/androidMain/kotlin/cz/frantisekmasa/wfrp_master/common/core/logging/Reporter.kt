package cz.frantisekmasa.wfrp_master.common.core.logging

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId

actual object Reporter {
    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    actual fun setUserId(id: UserId) {
        crashlytics.setUserId(id.toString())
    }

    actual fun log(message: String) {
        crashlytics.log(message)
    }

    actual fun recordThrowable(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    actual fun recordEvent(
        name: String,
        parameters: Map<String, String>,
    ) {
        Firebase.analytics.logEvent(name) {
            parameters.forEach { (name, value) ->
                param(name, value)
            }
        }
    }
}
