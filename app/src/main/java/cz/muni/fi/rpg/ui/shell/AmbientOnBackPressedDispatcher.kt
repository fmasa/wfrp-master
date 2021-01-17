package cz.muni.fi.rpg.ui.shell

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.staticAmbientOf

/**
 * Use [ProvideActivity] to pass this
 */
val AmbientOnBackPressedDispatcher = staticAmbientOf<OnBackPressedDispatcher> {
    error("AmbientOnBackPressedDispatcher was not initialized. Did you forget to call ProvideActivity?")
}