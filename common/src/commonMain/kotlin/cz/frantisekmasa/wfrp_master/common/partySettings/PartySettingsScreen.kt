package cz.frantisekmasa.wfrp_master.common.partySettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class PartySettingsScreen(
    private val partyId: PartyId,
): Screen {
    @Composable
    override fun Content() {
        val viewModel: PartySettingsScreenModel = rememberScreenModel(arg = partyId)
        val strings = LocalStrings.current

        Scaffold(
            topBar = {
                val navigator = LocalNavigator.currentOrThrow

                TopAppBar(
                    navigationIcon = { BackButton(onClick = navigator::pop) },
                    title = { Text(strings.parties.titleSettings) },
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
                    SettingsTitle(strings.parties.titleSettingsGeneral)
                    PartyNameItem(party.name, viewModel)

                    SettingsTitle(strings.combat.title)
                    InitiativeStrategyItem(party, viewModel)
                }
            }
        }
    }
}
