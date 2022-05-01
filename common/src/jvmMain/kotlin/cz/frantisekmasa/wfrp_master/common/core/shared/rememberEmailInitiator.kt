package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalEmailInitiator = staticCompositionLocalOf<EmailInitiator> { error("LocalEmailInitiator was not set") }

@Composable
actual fun rememberEmailInitiator(): EmailInitiator = LocalEmailInitiator.current
