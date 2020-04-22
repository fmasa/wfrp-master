package cz.muni.fi.rpg.di

import cz.muni.fi.rpg.ui.partyList.AssemblePartyDialog
import cz.muni.fi.rpg.ui.partyList.QrScanner
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun assemblePartyDialog(): AssemblePartyDialog

    @ContributesAndroidInjector
    abstract fun assembleQrScanner(): QrScanner
}