package cz.muni.fi.rpg.ui.common

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.BackButton
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing

@Composable
fun AboutScreen(routing: Routing<Route.About>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = { BackButton(onClick = { routing.backStack.pop() }) }
            )
        }
    ) {
        ScrollableColumn {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp)
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.h4)
                Text(BuildConfig.VERSION_NAME, Modifier.padding(bottom = 8.dp))
                Text(stringResource(R.string.about_body), textAlign = TextAlign.Center)

                Divider(Modifier.padding(vertical = 12.dp))

                Text(
                    stringResource(R.string.title_about_icons),
                    style = MaterialTheme.typography.h5,
                )
                Text(stringResource(R.string.iconsAttribution))
            }
        }
    }
}
