package cz.muni.fi.rpg.ui.shell

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cz.frantisekmasa.wfrp_master.common.appModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI

@Composable
fun ProvideDIContainer(content: @Composable () -> Unit) {
    val context = LocalContext.current

    withDI(
        remember(context) { createContainer(context) },
        content = content,
    )
}

fun createContainer(context: Context): DI {
    return DI {
        bindSingleton { context }
        import(appModule)
    }
}
