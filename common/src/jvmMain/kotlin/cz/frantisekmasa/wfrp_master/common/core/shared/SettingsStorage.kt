package cz.frantisekmasa.wfrp_master.common.core.shared

import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
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

    actual suspend fun <T> edit(key: SettingsKey<T>, update: (T?) -> T?) {
        val newValue = update(key.get(preferences))

        if (newValue == null) {
            preferences.remove(key.name)
        } else {
            key.set(preferences, newValue)
        }

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
    val name: String,
    val set: (Preferences, T) -> Unit,
    val get: (Preferences) -> T?
)

actual fun booleanSettingsKey(name: String): SettingsKey<Boolean> = SettingsKey(
    name = name,
    get = { if (name in it.keys()) it.getBoolean(name, false) else null },
    set = { preferences, value -> preferences.putBoolean(name, value) },
)

actual fun stringSetKey(name: String): SettingsKey<Set<String>> = SettingsKey(
    name = name,
    get = { preferences ->
        if (name in preferences.keys())
            preferences.get(name, "")
                .split(',')
                .map { it.decodeBase64String() }
                .toSet()
        else null
    },
    set = { preferences, value ->
        preferences.put(
            name,
            value.joinToString(",") { it.encodeBase64() },
        )
    },
)

actual fun stringKey(name: String): SettingsKey<String> = SettingsKey(
    name = name,
    get = { if (name in it.keys()) it.get(name, "") else null },
    set = { preferences, value -> preferences.put(name, value) },
)
