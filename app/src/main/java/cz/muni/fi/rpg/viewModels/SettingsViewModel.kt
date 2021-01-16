package cz.muni.fi.rpg.viewModels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsViewModel(
    context: Context,
    private val parties: PartyRepository,
) : ViewModel() {
    val darkMode: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
        preferences[Preferences.DARK_MODE] ?: false
    }

    val soundEnabled: Flow<Boolean>
        get() = dataStore.data.map { preferences -> preferences[Preferences.SOUND_ENABLED] ?: true }

    private val dataStore = context.createDataStore("settings")

    suspend fun getPartyNames(userId: String): List<String> {
        return parties.forUser(userId).map { it.getName() }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        dataStore.edit { it[Preferences.DARK_MODE] = enabled }
    }

    suspend fun toggleSound(enabled: Boolean) {
        dataStore.edit { it[Preferences.SOUND_ENABLED] = enabled }
    }
}

private object Preferences {
    val DARK_MODE = preferencesKey<Boolean>("dark_mode")
    val SOUND_ENABLED = preferencesKey<Boolean>("sound_enabled")
}

@Composable
fun provideSettingsViewModel(): SettingsViewModel {
    return AmbientActivity.current.getViewModel()
}