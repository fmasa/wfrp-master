package cz.muni.fi.rpg.ui.settings

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.BackButton
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.viewModel
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
import cz.muni.fi.rpg.viewModels.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
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
        }
    }
}

@Composable
private fun DarkModeCard(viewModel: SettingsViewModel) {
    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            Text(stringResource(R.string.settings_dark_mode))

            val darkModeEnabled by viewModel.darkMode.collectAsState(null)
            val coroutineScope = rememberCoroutineScope()

            Switch(
                checked = darkModeEnabled == true,
                enabled = darkModeEnabled != null,
                onCheckedChange = {
                    darkModeEnabled?.let { currentState ->
                        coroutineScope.launch {
                            viewModel.toggleDarkMode(!currentState)
                        }
                    }
                }
            )
        }
    }
}
