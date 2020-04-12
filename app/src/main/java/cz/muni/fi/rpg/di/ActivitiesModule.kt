package cz.muni.fi.rpg.di

import cz.muni.fi.rpg.GameMasterActivity
import cz.muni.fi.rpg.MainActivity
import cz.muni.fi.rpg.partyList.PartyListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun partyListActivity(): PartyListActivity

    @ContributesAndroidInjector
    abstract fun gameMasterActivity(): GameMasterActivity
}