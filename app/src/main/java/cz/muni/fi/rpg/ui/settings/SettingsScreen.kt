package cz.muni.fi.rpg.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.core.viewModel.SettingsViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.provideSettingsViewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(routing: Routing<Route.Settings>) {
    val strings = LocalStrings.current.settings

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = { routing.pop() })
                },
                title = { Text(strings.title) }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colors.background)
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            val viewModel = provideSettingsViewModel()

            SignInCard(viewModel, routing)

            SettingsCard {
                SettingsTitle(strings.titleGeneral)
                SoundCard(viewModel)
                DarkModeCard(viewModel)
            }
        }
    }
}

@Composable
private fun DarkModeCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = LocalStrings.current.settings.darkMode,
        value = viewModel.darkMode.collectWithLifecycle(null).value ?: isSystemInDarkTheme(),
        onChange = { viewModel.toggleDarkMode(it) },
    )
}

@Composable
private fun SoundCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = LocalStrings.current.settings.sound,
        value = viewModel.soundEnabled.collectWithLifecycle(null).value ?: false,
        onChange = { viewModel.toggleSound(it) }
    )
}

@Composable
private fun SwitchItem(
    name: String,
    value: Boolean?,
    onChange: suspend (newValue: Boolean) -> Unit,
    disabledText: String? = null,
    enabled: Boolean = true,
) {
    val color = LocalContentColor.current.copy(
        alpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled
    )

    ListItem(
        text = {
            Text(name, color = color)
        },
        secondaryText = disabledText?.let { { Text(disabledText, color = color) } },
        trailing = {
            val checked = value ?: return@ListItem
            val coroutineScope = rememberCoroutineScope()

            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = {
                    coroutineScope.launch {
                        onChange(!checked)
                    }
                }
            )
        }
    )
}
