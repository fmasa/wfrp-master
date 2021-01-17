package cz.muni.fi.rpg.ui.shell

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.staticAmbientOf

val AmbientOnBackPressedDispatcher = staticAmbientOf<OnBackPressedDispatcher> {
    error("AmbientOnBackPressedDispatcher was not initialized")
}