package cz.muni.fi.rpg.ui.partySettings

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import org.koin.core.parameter.parametersOf

@Composable
fun PartySettingsScreen(routing: Routing<Route.PartySettings>) {
    val viewModel: PartySettingsViewModel by viewModel { parametersOf(routing.route.partyId) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onClick = { routing.pop() }) },
                title = { Text(stringResource(R.string.title_party_settings)) },
            )
        },
    ) {
        val party = viewModel.party.collectAsState(null).value

        if (party == null) {
            FullScreenProgress()
            return@Scaffold
        }

        ScrollableColumn(
            contentPadding = PaddingValues(bottom = Spacing.medium)
        ) {
            Card(shape = MaterialTheme.shapes.large) {
                Column {
                    SectionTitle(R.string.party_section_general)
                    PartyNameItem(party.getName(), viewModel)

                    SectionTitle(R.string.title_combat)
                    InitiativeStrategyItem(party, viewModel)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        modifier = Modifier.padding(start = Spacing.large, top = Spacing.bodyPadding),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
    )
}