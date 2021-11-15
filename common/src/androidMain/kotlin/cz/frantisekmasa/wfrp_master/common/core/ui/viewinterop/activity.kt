package cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<AppCompatActivity> {
    error("LocalActivity was not initialized. Did you forget to call ProvideActivity?")
}
