package cz.frantisekmasa.wfrp_master.common.core.viewModel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.core.ads.AdManager
import cz.frantisekmasa.wfrp_master.common.core.ads.LocationProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel

private val Context.settingsDataStore by preferencesDataStore("settings")

class SettingsViewModel(
    context: Context,
    private val parties: PartyRepository,
    private val locationProvider: LocationProvider,
    private val adManager: AdManager,
) : ViewModel() {

    val darkMode: Flow<Boolean?> by lazy { getPreference(AppSettings.DARK_MODE) }
    val soundEnabled: Flow<Boolean?> by lazy { getPreference(AppSettings.SOUND_ENABLED) }
    val personalizedAds: Flow<Boolean?> by lazy { getPreference(AppSettings.PERSONALIZED_ADS, false) }

    private val dataStore = context.settingsDataStore

    suspend fun initializeAds() {
        val personalizedAds = refreshPersonalizedAdConsent()

        withContext(Dispatchers.Main) {
            adManager.initialize(personalizedAds)
        }
    }

    suspend fun getPartyNames(userId: String): List<String> {
        return parties.forUser(userId).map { it.getName() }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        dataStore.edit { it[AppSettings.DARK_MODE] = enabled }
    }

    suspend fun toggleSound(enabled: Boolean) {
        dataStore.edit { it[AppSettings.SOUND_ENABLED] = enabled }
    }

    suspend fun togglePersonalizedAds(enabled: Boolean) {
        Firebase.analytics.logEvent(
            if (enabled) "personalized_ads_enabled" else "personalized_ads_disabled",
            null,
        )
        dataStore.edit { it[AppSettings.PERSONALIZED_ADS] = enabled }
    }

    fun userDismissedGoogleSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { it[AppSettings.GOOGLE_SIGN_IN_DISMISSED] = true }
        }
    }

    private suspend fun refreshPersonalizedAdConsent(): Boolean {
        val personalizedAdsAllowed = dataStore.data.first()[AppSettings.PERSONALIZED_ADS]

        if (personalizedAdsAllowed != null) {
            return personalizedAdsAllowed
        }

        val personalizedAds = !locationProvider.isUserInEeaOrUnknown()

        Napier.d("Checked whether we can show personalized ads to user. Result: $personalizedAds")

        dataStore.edit { it[AppSettings.PERSONALIZED_ADS] = personalizedAds }

        return personalizedAds
    }

    private fun getPreference(
        preference: Preferences.Key<Boolean>,
        defaultValue: Boolean
    ): Flow<Boolean> {
        return dataStore.data
            .map { preferences -> preferences[preference] ?: defaultValue }
    }

    private fun getPreference(preference: Preferences.Key<Boolean>): Flow<Boolean?> {
        return dataStore.data
            .map { preferences -> preferences[preference] }
    }
}

private object AppSettings {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    val GOOGLE_SIGN_IN_DISMISSED = booleanPreferencesKey("dismissed_google_sign_in")
    val PERSONALIZED_ADS = booleanPreferencesKey("personalized_ads")
}

@Composable
fun provideSettingsViewModel(): SettingsViewModel {
    return LocalActivity.current.getViewModel()
}
