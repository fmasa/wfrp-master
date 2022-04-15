package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalUrlOpener = staticCompositionLocalOf<UrlOpener> { error("LocalUrlOpener was not set") }

@Composable
actual fun rememberUrlOpener(): UrlOpener = LocalUrlOpener.current
