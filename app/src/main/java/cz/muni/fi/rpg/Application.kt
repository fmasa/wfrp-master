package cz.muni.fi.rpg

import cz.frantisekmasa.wfrp_master.common.core.logging.ErrorReportingAntilog
import cz.frantisekmasa.wfrp_master.common.core.logging.KoinNapierLogger
import cz.muni.fi.rpg.di.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@Application)
            logger(KoinNapierLogger(Level.ERROR))
            // declare modules
            modules(appModule)
        }

        Napier.base(
            if (BuildConfig.DEBUG)
                DebugAntilog()
            else ErrorReportingAntilog()
        )
    }
}
