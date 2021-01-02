package cz.muni.fi.rpg.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(routing: Routing<Route.Settings>) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = { routing.backStack.pop() })
                },
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) {
        ScrollableColumn(Modifier.background(MaterialTheme.colors.background).padding(top = 6.dp)) {
            val viewModel: SettingsViewModel by viewModel()

            SignInCard(viewModel)
            DarkModeCard(viewModel)
            SoundCard(viewModel)
        }
    }
}

@Composable
private fun DarkModeCard(viewModel: SettingsViewModel) {
    SwitchCard(
        name = R.string.settings_dark_mode,
        value = viewModel.darkMode,
        onChange = { viewModel.toggleDarkMode(it) }
    )
}

@Composable
private fun SoundCard(viewModel: SettingsViewModel) {
    SwitchCard(
        name = R.string.settings_sound,
        value = viewModel.soundEnabled,
        onChange = { viewModel.toggleSound(it) }
    )
}

@Composable
private fun SwitchCard(
    @StringRes name: Int,
    value: Flow<Boolean>,
    onChange: suspend (newValue: Boolean) -> Unit
) {
    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            Text(stringResource(name))

            val enabled by value.collectAsState(null)
            val coroutineScope = rememberCoroutineScope()

            Switch(
                checked = enabled == true,
                enabled = enabled != null,
                onCheckedChange = {
                    enabled?.let { currentState ->
                        coroutineScope.launch {
                            onChange(!currentState)
                        }
                    }
                }
            )
        }
    }
}
