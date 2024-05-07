package cz.frantisekmasa.wfrp_master.common.partySettings

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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.SelectionDialog
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
internal fun InitiativeStrategyItem(
    party: Party,
    viewModel: PartySettingsScreenModel,
) {
    val strategy = party.settings.initiativeStrategy

    val coroutineScope = rememberCoroutineScope()
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        InitiativeStrategyDialog(
            selected = strategy,
            onSelect = { newStrategy ->
                coroutineScope.launchLogged(Dispatchers.IO) {
                    viewModel.updateSettings { it.copy(initiativeStrategy = newStrategy) }
                    dialogVisible = false
                }
            },
            onDismissRequest = { dialogVisible = false },
        )
    }

    ListItem(
        text = { Text(stringResource(Str.combat_initiative_strategy_config_option)) },
        secondaryText = { Text(strategy.localizedName) },
        modifier =
            Modifier.clickable {
                dialogVisible = true
            },
    )
}

@Composable
private fun InitiativeStrategyDialog(
    selected: InitiativeStrategy,
    onSelect: (InitiativeStrategy) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SelectionDialog(
        title = stringResource(Str.combat_initiative_strategy_prompt),
        items = InitiativeStrategy.values().toList(),
        selected = selected,
        onDismissRequest = onDismissRequest,
        onSelect = onSelect,
    ) { strategy ->
        Text(strategy.localizedName)
    }
}
