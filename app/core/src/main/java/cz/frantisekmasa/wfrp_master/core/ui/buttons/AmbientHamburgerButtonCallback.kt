package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.compose.runtime.staticAmbientOf

val AmbientHamburgerButtonHandler =
    staticAmbientOf<() -> Unit> { error("Hamburger button callback not set") }