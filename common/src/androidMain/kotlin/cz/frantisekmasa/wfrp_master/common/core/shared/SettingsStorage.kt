package cz.frantisekmasa.wfrp_master.common.core.shared

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("settings")

actual class SettingsStorage(context: Context) {
    private val storage = context.settingsDataStore

    actual suspend fun <T> edit(
        key: SettingsKey<T>,
        update: (T?) -> T?,
    ) {
        storage.edit {
            val newValue = update(it[key])

            if (newValue == null) {
                it.remove(key)
            } else {
                it[key] = newValue
            }
        }
    }

    actual fun <T> watch(key: SettingsKey<T>): Flow<T?> {
        return storage.data.map { it[key] }
    }
}

actual typealias SettingsKey<T> = Preferences.Key<T>

actual fun booleanSettingsKey(name: String): SettingsKey<Boolean> = booleanPreferencesKey(name)

actual fun stringSetKey(name: String): SettingsKey<Set<String>> = stringSetPreferencesKey(name)

actual fun stringKey(name: String): SettingsKey<String> = stringPreferencesKey(name)
