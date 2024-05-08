package cz.muni.fi.rpg

import cz.frantisekmasa.wfrp_master.common.BuildKonfig
import cz.frantisekmasa.wfrp_master.common.core.logging.ErrorReportingAntilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(
            if (BuildKonfig.isDebugMode) {
                DebugAntilog()
            } else {
                ErrorReportingAntilog()
            },
        )
    }
}
