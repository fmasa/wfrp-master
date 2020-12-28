package cz.muni.fi.rpg

import com.google.firebase.crashlytics.FirebaseCrashlytics
import cz.frantisekmasa.wfrp_master.core.logging.CrashlyticsTree
import cz.frantisekmasa.wfrp_master.core.logging.KoinTimberLogger
import cz.muni.fi.rpg.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            fragmentFactory()

            // declare used Android context
            androidContext(this@Application)
            logger(KoinTimberLogger(Level.ERROR))
            // declare modules
            modules(appModule)
        }

        Timber.plant(
            if (BuildConfig.DEBUG)
                Timber.DebugTree()
            else CrashlyticsTree(FirebaseCrashlytics.getInstance())
        )
    }
}