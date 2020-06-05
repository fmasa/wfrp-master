package cz.muni.fi.rpg

import cz.muni.fi.rpg.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            fragmentFactory()

            // declare used Android context
            androidContext(this@Application)
            androidLogger()
            // declare modules
            modules(appModule)
        }
    }
}