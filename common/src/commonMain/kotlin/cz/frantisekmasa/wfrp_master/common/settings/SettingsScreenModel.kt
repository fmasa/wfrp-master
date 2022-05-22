package cz.frantisekmasa.wfrp_master.common.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsKey
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.booleanSettingsKey
import cz.frantisekmasa.wfrp_master.common.core.shared.edit
import kotlinx.coroutines.flow.Flow

class SettingsScreenModel(
    private val parties: PartyRepository,
    private val storage: SettingsStorage,
) : ScreenModel {

    val darkMode: Flow<Boolean?> by lazy { getPreference(AppSettings.DARK_MODE) }
    val soundEnabled: Flow<Boolean?> by lazy { getPreference(AppSettings.SOUND_ENABLED) }

    suspend fun getPartyNames(userId: String): List<String> {
        return parties.forUser(userId).map { it.name }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        storage.edit(AppSettings.DARK_MODE, enabled)
    }

    suspend fun toggleSound(enabled: Boolean) {
        storage.edit(AppSettings.SOUND_ENABLED, enabled)
    }

    private fun getPreference(preference: SettingsKey<Boolean>): Flow<Boolean?> {
        return storage.watch(preference)
    }
}

object AppSettings {
    val DARK_MODE = booleanSettingsKey("dark_mode")
    val SOUND_ENABLED = booleanSettingsKey("sound_enabled")
    val GOOGLE_SIGN_IN_DISMISSED = booleanSettingsKey("dismissed_google_sign_in")
}
