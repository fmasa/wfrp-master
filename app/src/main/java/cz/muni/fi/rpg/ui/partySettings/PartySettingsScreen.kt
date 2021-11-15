package cz.muni.fi.rpg.ui.partySettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.components.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.components.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
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
        val party = viewModel.party.collectWithLifecycle(null).value

        if (party == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.verticalScroll(rememberScrollState())) {
            SettingsCard {
                SettingsTitle(R.string.party_section_general)
                PartyNameItem(party.getName(), viewModel)

                SettingsTitle(R.string.title_combat)
                InitiativeStrategyItem(party, viewModel)
            }
        }
    }
}
