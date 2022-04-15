package cz.frantisekmasa.wfrp_master.common.core.shared

import kotlinx.coroutines.flow.Flow

expect class SettingsStorage {
    suspend fun <T> edit(key: SettingsKey<T>, value: T)

    fun <T> watch(key: SettingsKey<T>): Flow<T?>
}


expect class SettingsKey<T>

expect fun booleanSettingsKey(name: String): SettingsKey<Boolean>