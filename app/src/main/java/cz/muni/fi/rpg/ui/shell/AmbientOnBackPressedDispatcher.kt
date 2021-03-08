package cz.muni.fi.rpg.ui.shell

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Use [ProvideActivity] to pass this
 */
val LocalOnBackPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcher> {
    error("LocalOnBackPressedDispatcher was not initialized. Did you forget to call ProvideActivity?")
}