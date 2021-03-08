package cz.muni.fi.rpg.ui.shell

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cz.frantisekmasa.wfrp_master.core.ui.theme.LocalSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.theme.rememberSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.LocalActivity

@Composable
fun ProvideActivity(activity: AppCompatActivity, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalActivity provides activity,
        LocalSystemUiController provides rememberSystemUiController(activity.window),
        LocalOnBackPressedDispatcher provides activity.onBackPressedDispatcher,
        content = content,
    )
}