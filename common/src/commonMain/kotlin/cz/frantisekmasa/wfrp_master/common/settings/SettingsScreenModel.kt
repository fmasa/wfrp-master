package cz.frantisekmasa.wfrp_master.common.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsKey
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.booleanSettingsKey
import cz.frantisekmasa.wfrp_master.common.core.shared.edit
import cz.frantisekmasa.wfrp_master.common.core.shared.stringKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsScreenModel(
    private val parties: PartyRepository,
    private val storage: SettingsStorage,
) : ScreenModel {
    val darkMode: Flow<Boolean?> by lazy { getPreference(AppSettings.DARK_MODE) }
    val soundEnabled: Flow<Boolean?> by lazy { getPreference(AppSettings.SOUND_ENABLED) }
    val keepScreenOn: Flow<Boolean?> by lazy { getPreference(AppSettings.KEEP_SCREEN_ON) }
    val lastSeenVersion: Flow<String?> by lazy { storage.watch(AppSettings.LAST_SEEN_VERSION) }
    val language: Flow<Language?> by lazy {
        storage.watch(AppSettings.LANGUAGE).map {
            it?.let { code -> Language.fromCodeOrNull(code) }
        }
    }

    suspend fun getPartyNames(userId: UserId): List<String> {
        return parties.forUser(userId).map { it.name }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        storage.edit(AppSettings.DARK_MODE, enabled)
    }

    suspend fun toggleSound(enabled: Boolean) {
        storage.edit(AppSettings.SOUND_ENABLED, enabled)
    }

    suspend fun toggleKeepScreenOn(enabled: Boolean) {
        storage.edit(AppSettings.KEEP_SCREEN_ON, enabled)
    }

    suspend fun updateLastSeenVersion(version: String) {
        storage.edit(AppSettings.LAST_SEEN_VERSION, version)
    }

    suspend fun updateLanguage(language: Language) {
        Reporting.record { appLanguageChanged(language.name) }
        storage.edit(AppSettings.LANGUAGE, language.name)
    }

    private fun getPreference(preference: SettingsKey<Boolean>): Flow<Boolean?> {
        return storage.watch(preference)
    }
}

object AppSettings {
    val DARK_MODE = booleanSettingsKey("dark_mode")
    val SOUND_ENABLED = booleanSettingsKey("sound_enabled")
    val KEEP_SCREEN_ON = booleanSettingsKey("keep_screen_on")
    val GOOGLE_SIGN_IN_DISMISSED = booleanSettingsKey("dismissed_google_sign_in")
    val LAST_SEEN_VERSION = stringKey("last_seen_version")
    val LANGUAGE = stringKey("language")
}
