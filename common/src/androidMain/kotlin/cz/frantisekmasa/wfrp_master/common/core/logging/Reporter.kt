package cz.frantisekmasa.wfrp_master.common.core.logging

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.analytics.logEvent
import dev.gitlive.firebase.crashlytics.crashlytics

actual object Reporter {
    private val crashlytics by lazy { Firebase.crashlytics }

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
