package cz.muni.fi.rpg.ui.shell

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import cz.frantisekmasa.wfrp_master.core.ui.theme.AmbientSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.theme.rememberSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity

@Composable
fun ProvideActivity(activity: AppCompatActivity, content: @Composable () -> Unit) {
    Providers(
        AmbientActivity provides activity,
        AmbientSystemUiController provides rememberSystemUiController(activity.window),
        AmbientOnBackPressedDispatcher provides activity.onBackPressedDispatcher,
        content = content,
    )
}