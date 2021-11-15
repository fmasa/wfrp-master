package cz.muni.fi.rpg.ui.partySettings

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.SelectionDialog
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
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
            onDismissRequest = { dialogVisible = false }
        )
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
