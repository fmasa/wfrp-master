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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.BuildConfig

@Composable
fun AboutScreen(routing: Routing<Route.About>) {
    val strings = LocalStrings.current.about

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.title) },
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
                Text(strings.appName, style = MaterialTheme.typography.h4)
                Text(BuildConfig.VERSION_NAME, Modifier.padding(bottom = 8.dp))
                Text(strings.body, textAlign = TextAlign.Center)

                Divider(Modifier.padding(vertical = 12.dp))

                Text(
                    strings.titleAttribution,
                    style = MaterialTheme.typography.h6,
                )

                listOf(strings.attributionIcons, strings.attributionDiceRollSound).forEach {
                    Text(it, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
