package cz.muni.fi.rpg.viewModels

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsViewModel(
    context: Context,
    private val parties: PartyRepository,
) : ViewModel() {
    val darkMode: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
        preferences[Preferences.DARK_MODE] ?: false
    }

    private val dataStore = context.createDataStore("settings")

    suspend fun getPartyNames(userId: String): List<String> {
        return parties.forUser(userId).map { it.getName() }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        dataStore.edit { it[Preferences.DARK_MODE] = enabled }
    }
}

private object Preferences {
    val DARK_MODE = preferencesKey<Boolean>("dark_mode")
}