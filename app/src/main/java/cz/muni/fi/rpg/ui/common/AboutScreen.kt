package cz.muni.fi.rpg.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.R

@Composable
fun AboutScreen(routing: Routing<Route.About>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = { BackButton(onClick = { routing.pop() }) }
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
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
                    stringResource(R.string.title_about_attribution),
                    style = MaterialTheme.typography.h6,
                )

                listOf(R.string.attribution_icons, R.string.attribution_dice_roll_sound).forEach {
                    Text(stringResource(it), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
