package cz.frantisekmasa.wfrp_master.common.core.shared

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

actual class SettingsStorage() {
    private val preferences = Preferences.userRoot().node("wfrp-master")

    private val data = callbackFlow<Preferences> {
        trySend(preferences)

        val listener = PreferenceChangeListener {
            trySend(it.node)
        }

        preferences.addPreferenceChangeListener(listener)

        awaitClose { preferences.removePreferenceChangeListener(listener) }
    }

    actual suspend fun <T> edit(key: SettingsKey<T>, value: T) {
        key.set(preferences, value)
        preferences.sync()
    }

    operator fun <T> get(key: SettingsKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return key.get(preferences)
    }

    actual fun <T> watch(key: SettingsKey<T>): Flow<T?> {
        @Suppress("UNCHECKED_CAST")
        return data.map { key.get(it) }
    }
}

actual class SettingsKey<T>(
    val set: (Preferences, T) -> Unit,
    val get: (Preferences) -> T?
)


data class BooleanSettingsKey(
    val name: String
)


actual fun booleanSettingsKey(name: String): SettingsKey<Boolean> = SettingsKey(
    get = { if (name in it.keys()) it.getBoolean(name, false) else null },
    set = { preferences, value -> preferences.putBoolean(name, value) },
)

fun stringSettingsKey(name: String): SettingsKey<String> = SettingsKey(
    get = { if (name in it.keys()) it.get(name, "") else null },
    set = { preferences, value -> preferences.put(name, value) },
)
