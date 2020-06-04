package cz.muni.fi.rpg

import cz.muni.fi.rpg.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@Application)
            // declare modules
            modules(appModule)
        }
    }
}