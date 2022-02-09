package cz.frantisekmasa.wfrp_master.common.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual object Reporter {
    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    actual fun setUserId(id: String) {
        crashlytics.setUserId(id)
    }

    actual fun log(message: String) {
        crashlytics.log(message)
    }

    actual fun recordThrowable(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}