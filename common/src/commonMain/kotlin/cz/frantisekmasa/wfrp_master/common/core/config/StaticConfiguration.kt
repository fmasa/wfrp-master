package cz.frantisekmasa.wfrp_master.common.core.config

import androidx.compose.runtime.Immutable

@Immutable
data class StaticConfiguration(
    val isProduction: Boolean,
    val version: String,
    val platform: Platform,
)

enum class Platform {
    Android,
    Desktop,
}