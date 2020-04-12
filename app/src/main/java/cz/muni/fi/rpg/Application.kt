package cz.muni.fi.rpg

import cz.muni.fi.rpg.di.DaggerApplicationComponent
import dagger.android.DaggerApplication

class Application : DaggerApplication() {
    override fun applicationInjector() = DaggerApplicationComponent.create()!!
}