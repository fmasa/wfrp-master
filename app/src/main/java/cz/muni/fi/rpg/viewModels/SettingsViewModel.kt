package cz.muni.fi.rpg.viewModels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsViewModel(
    context: Context,
    private val parties: PartyRepository,
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val darkMode: StateFlow<Boolean> by lazy { getPreference(AppSettings.DARK_MODE, false) }
    val soundEnabled: StateFlow<Boolean> by lazy { getPreference(AppSettings.SOUND_ENABLED, true) }

    private val dataStore = context.createDataStore("settings")

    suspend fun getPartyNames(userId: String): List<String> {
        return parties.forUser(userId).map { it.getName() }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        dataStore.edit { it[AppSettings.DARK_MODE] = enabled }
    }

    suspend fun toggleSound(enabled: Boolean) {
        dataStore.edit { it[AppSettings.SOUND_ENABLED] = enabled }
    }

    fun userDismissedGoogleSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { it[AppSettings.GOOGLE_SIGN_IN_DISMISSED] = true }
        }
    }

    private fun getPreference(
        preference: Preferences.Key<Boolean>,
        defaultValue: Boolean
    ): StateFlow<Boolean> {
        return dataStore.data
            .map { preferences -> preferences[preference] ?: defaultValue }
            .stateIn(this, SharingStarted.Eagerly, defaultValue)
    }
}

private object AppSettings {
    val DARK_MODE = preferencesKey<Boolean>("dark_mode")
    val SOUND_ENABLED = preferencesKey<Boolean>("sound_enabled")
    val GOOGLE_SIGN_IN_DISMISSED = preferencesKey<Boolean>("dismissed_google_sign_in")
}

@Composable
fun provideSettingsViewModel(): SettingsViewModel {
    return AmbientActivity.current.getViewModel()
}