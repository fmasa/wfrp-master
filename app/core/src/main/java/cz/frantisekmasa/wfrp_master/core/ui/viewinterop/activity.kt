package cz.frantisekmasa.wfrp_master.core.ui.viewinterop

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.platform.AmbientContext

@Deprecated(
    "Use AmbientActivity",
    ReplaceWith(
        "AmbientActivity.current",
        "cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity"
    )
)
@Composable
fun activity(): AppCompatActivity {
    val context = AmbientContext.current

    check(context is AppCompatActivity)

    return context
}

/**
 * Use [ProvideActivity] to pass this
 */
val AmbientActivity = staticAmbientOf<AppCompatActivity> {
    error("AmbientActivity was not initialized. Did you forget to call ProvideActivity?")
}