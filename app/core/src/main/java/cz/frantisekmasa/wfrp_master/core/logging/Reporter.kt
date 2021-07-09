package cz.frantisekmasa.wfrp_master.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics

class Reporter {
    companion object {
        private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

        fun setUserId(id: String) {
            crashlytics.setUserId(id)
        }
    }
}
