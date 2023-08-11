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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import dev.icerock.moko.resources.compose.stringResource

class PartySettingsScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: PartySettingsScreenModel = rememberScreenModel(arg = partyId)

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(stringResource(Str.parties_title_settings)) },
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
                    SettingsTitle(stringResource(Str.parties_title_settings_general))
                    PartyNameItem(party.name, viewModel)

                    SettingsTitle(stringResource(Str.combat_title))
                    InitiativeStrategyItem(party, viewModel)
                    AdvantageSystemItem(party.settings, viewModel)
                    AdvantageCapItem(party.settings, viewModel)
                }
            }
        }
    }
}
