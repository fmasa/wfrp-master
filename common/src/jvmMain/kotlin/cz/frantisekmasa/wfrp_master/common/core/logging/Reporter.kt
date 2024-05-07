package cz.frantisekmasa.wfrp_master.common.core.logging

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId

// TODO: Add Sentry?
actual object Reporter {
    actual fun setUserId(id: UserId) {
    }

    actual fun log(message: String) {
    }

    actual fun recordThrowable(throwable: Throwable) {
    }

    actual fun recordEvent(
        name: String,
        parameters: Map<String, String>,
    ) {
    }
}
