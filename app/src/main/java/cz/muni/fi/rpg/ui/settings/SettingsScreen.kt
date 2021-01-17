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
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.SettingsViewModel
import cz.muni.fi.rpg.viewModels.provideSettingsViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(routing: Routing<Route.Settings>) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = { routing.pop() })
                },
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) {
        ScrollableColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .padding(top = 6.dp)) {
            val viewModel = provideSettingsViewModel()

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
    value: StateFlow<Boolean>,
    onChange: suspend (newValue: Boolean) -> Unit
) {
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Text(stringResource(name))

            val enabled by value.collectAsState()
            val coroutineScope = rememberCoroutineScope()

            Switch(
                checked = enabled,
                onCheckedChange = {
                    enabled.let { currentState ->
                        coroutineScope.launch {
                            onChange(!currentState)
                        }
                    }
                }
            )
        }
    }
}
