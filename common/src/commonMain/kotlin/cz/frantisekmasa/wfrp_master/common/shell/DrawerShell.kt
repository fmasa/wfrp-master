package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.LocalHamburgerButtonHandler
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.settings.AppSettings
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
@ExperimentalMaterialApi
fun DrawerShell(
    drawerState: DrawerState,
    bodyContent: @Composable () -> Unit,
) {
    val settings: SettingsStorage by localDI().instance()
    val language =
        remember {
            settings.watch(AppSettings.LANGUAGE)
                .map { code -> code?.let { Language.fromCodeOrNull(it) } ?: Language.EN }
        }.collectWithLifecycle(null).value

    if (language == null) {
        FullScreenProgress()
        return
    }

    SideEffect {
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.name)
    }

    key(language) {
        ModalDrawer(
            modifier = Modifier.imePadding(),
            drawerState = drawerState,
            drawerContent = {
                Column {
                    AppDrawer(drawerState)
                }
            },
            content = {
                val coroutineScope = rememberCoroutineScope()

                CompositionLocalProvider(
                    LocalHamburgerButtonHandler provides { coroutineScope.launch { drawerState.open() } },
                    content = {
                        HorizontalInsetsContainer {
                            Column {
                                Surface(
                                    color = MaterialTheme.colors.primarySurface,
                                    elevation = AppBarDefaults.TopAppBarElevation,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                                }

                                Box(
                                    Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars),
                                ) {
                                    bodyContent()
                                }
                            }
                        }
                    },
                )
            },
        )
    }
}
