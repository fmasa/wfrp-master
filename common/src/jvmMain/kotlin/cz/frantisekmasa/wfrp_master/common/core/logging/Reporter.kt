package cz.frantisekmasa.wfrp_master.common.core.logging

// TODO: Add Sentry?
actual object Reporter {
    actual fun setUserId(id: String) {
    }

    actual fun log(message: String) {
    }

    actual fun recordThrowable(throwable: Throwable) {
    }

    actual fun recordEvent(name: String, parameters: Map<String, String>) {
    }
}
