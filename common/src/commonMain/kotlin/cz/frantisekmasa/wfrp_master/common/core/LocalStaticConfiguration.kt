package cz.frantisekmasa.wfrp_master.common.core

import androidx.compose.runtime.staticCompositionLocalOf
import cz.frantisekmasa.wfrp_master.common.core.config.StaticConfiguration

val LocalStaticConfiguration =
    staticCompositionLocalOf<StaticConfiguration> {
        error("Static configuration was not provided")
    }
