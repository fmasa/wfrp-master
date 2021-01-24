package cz.frantisekmasa.wfrp_master.core.viewModel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.frantisekmasa.wfrp_master.core.ads.LocationProvider
import cz.frantisekmasa.wfrp_master.core.ads.AdManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class SettingsViewModel(
    context: Context,
    private val parties: PartyRepository,
    private val locationProvider: LocationProvider,
    private val adManager: AdManager,
) : ViewModel() {

    val darkMode: StateFlow<Boolean> by lazy { getPreference(AppSettings.DARK_MODE, false) }
    val soundEnabled: StateFlow<Boolean> by lazy { getPreference(AppSettings.SOUND_ENABLED, true) }
    val personalizedAds: StateFlow<Boolean> by lazy { getPreference(AppSettings.PERSONALIZED_ADS, false) }

    private val dataStore = context.createDataStore("settings")

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

        Timber.d("Checked whether we can show personalized ads to user. Result: $personalizedAds")

        dataStore.edit { it[AppSettings.PERSONALIZED_ADS] = personalizedAds }

        return personalizedAds
    }

    private fun getPreference(
        preference: Preferences.Key<Boolean>,
        defaultValue: Boolean
    ): StateFlow<Boolean> {
        return dataStore.data
            .map { preferences -> preferences[preference] ?: defaultValue }
            .stateIn(viewModelScope, SharingStarted.Eagerly, defaultValue)
    }
}

private object AppSettings {
    val DARK_MODE = preferencesKey<Boolean>("dark_mode")
    val SOUND_ENABLED = preferencesKey<Boolean>("sound_enabled")
    val GOOGLE_SIGN_IN_DISMISSED = preferencesKey<Boolean>("dismissed_google_sign_in")
    val PERSONALIZED_ADS = preferencesKey<Boolean>("personalized_ads")
}

@Composable
fun provideSettingsViewModel(): SettingsViewModel {
    return AmbientActivity.current.getViewModel()
}