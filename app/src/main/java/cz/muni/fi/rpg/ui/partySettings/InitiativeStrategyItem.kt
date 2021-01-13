package cz.muni.fi.rpg.ui.partySettings

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.SelectionDialog
import cz.frantisekmasa.wfrp_master.core.utils.launchLogged
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun InitiativeStrategyItem(
    party: Party,
    viewModel: PartySettingsViewModel,
) {
    val strategy = party.getSettings().initiativeStrategy

    val coroutineScope = rememberCoroutineScope()
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        InitiativeStrategyDialog(
            selected = strategy,
            onSelect = { newStrategy ->
                coroutineScope.launchLogged(Dispatchers.IO) {
                    viewModel.updateSettings { it.copy(newStrategy) }
                    withContext(Dispatchers.Main) { dialogVisible = false }
                }
            },
            onDismissRequest = { dialogVisible = false })
    }

    ListItem(
        text = { Text(stringResource(R.string.initiative_rules)) },
        secondaryText = { Text(strategyLabel(strategy)) },
        modifier = Modifier.clickable {
            dialogVisible = true
        }
    )
}

@Composable
private fun InitiativeStrategyDialog(
    selected: InitiativeStrategy,
    onSelect: (InitiativeStrategy) -> Unit,
    onDismissRequest: () -> Unit
) {
    SelectionDialog(
        title = "Select Initiative rules",
        items = InitiativeStrategy.values().toList(),
        selected = selected,
        onDismissRequest = onDismissRequest,
        onSelect = onSelect
    ) { strategy ->
        Text(strategyLabel(strategy))
    }
}


@Composable
private fun strategyLabel(strategy: InitiativeStrategy) = stringResource(
    when (strategy) {
        InitiativeStrategy.INITIATIVE_CHARACTERISTIC -> R.string.initiative_initiative_characteristic
        InitiativeStrategy.INITIATIVE_TEST -> R.string.initiative_initiative_test
        InitiativeStrategy.INITIATIVE_PLUS_1D10 -> R.string.initiative_initiative_plus_1d10
        InitiativeStrategy.BONUSES_PLUS_1D10 -> R.string.initiative_bonuses_plus_1d10
    }
)