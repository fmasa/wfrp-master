package cz.muni.fi.rpg.ui.shell

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.LocalSystemUiController
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.rememberSystemUiController
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.network.LocalConnectivityChecker
import cz.frantisekmasa.wfrp_master.common.network.ReactiveNetworkConnectivityChecker

@Composable
fun ProvideActivity(
    activity: AppCompatActivity,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalActivity provides activity,
        LocalSystemUiController provides rememberSystemUiController(activity.window),
        LocalConnectivityChecker provides ReactiveNetworkConnectivityChecker(activity),
        content = content,
    )
}
