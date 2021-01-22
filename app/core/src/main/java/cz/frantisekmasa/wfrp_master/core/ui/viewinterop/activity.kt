package cz.frantisekmasa.wfrp_master.core.ui.viewinterop

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.staticAmbientOf

/**
 * Use [ProvideActivity] to pass this
 */
val AmbientActivity = staticAmbientOf<AppCompatActivity> {
    error("AmbientActivity was not initialized. Did you forget to call ProvideActivity?")
}