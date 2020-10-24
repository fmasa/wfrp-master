package cz.muni.fi.rpg.ui.settings

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.BackButton
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing

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
        ScrollableColumn(Modifier.background(MaterialTheme.colors.background)) {
            SignInCard()

        }
    }
}
