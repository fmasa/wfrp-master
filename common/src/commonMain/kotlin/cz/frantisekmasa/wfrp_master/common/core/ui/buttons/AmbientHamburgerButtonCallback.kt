package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.runtime.staticCompositionLocalOf

val LocalHamburgerButtonHandler =
    staticCompositionLocalOf<() -> Unit> { error("Hamburger button callback not set") }
