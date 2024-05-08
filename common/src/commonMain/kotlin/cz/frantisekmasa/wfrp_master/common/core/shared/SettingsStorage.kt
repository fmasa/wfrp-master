@file:JvmName("CommonSettingsStorage")

package cz.frantisekmasa.wfrp_master.common.core.shared

import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmName

expect class SettingsStorage {
    suspend fun <T> edit(
        key: SettingsKey<T>,
        update: (T?) -> T?,
    )

    fun <T> watch(key: SettingsKey<T>): Flow<T?>
}

suspend fun <T> SettingsStorage.edit(
    key: SettingsKey<T>,
    value: T?,
) {
    edit(key) { value }
}

expect class SettingsKey<T>

expect fun booleanSettingsKey(name: String): SettingsKey<Boolean>

expect fun stringSetKey(name: String): SettingsKey<Set<String>>

expect fun stringKey(name: String): SettingsKey<String>
