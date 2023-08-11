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
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.AdvantageSystem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.SelectionDialog
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun AdvantageSystemItem(
    settings: Settings,
    viewModel: PartySettingsScreenModel,
) {
    val currentSystem = settings.advantageSystem

    val coroutineScope = rememberCoroutineScope()
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        AdvantageSystemDialog(
            selected = currentSystem,
            onSelect = { newSystem ->
                coroutineScope.launchLogged(Dispatchers.IO) {
                    viewModel.updateSettings { it.copy(advantageSystem = newSystem) }
                    dialogVisible = false
                }
            },
            onDismissRequest = { dialogVisible = false }
        )
    }

    ListItem(
        text = { Text(stringResource(Str.combat_advantage_system_config_option)) },
        secondaryText = { Text(currentSystem.localizedName) },
        modifier = Modifier.clickable {
            dialogVisible = true
        }
    )
}

@Composable
private fun AdvantageSystemDialog(
    selected: AdvantageSystem,
    onSelect: (AdvantageSystem) -> Unit,
    onDismissRequest: () -> Unit
) {
    SelectionDialog(
        title = stringResource(Str.combat_advantage_system_prompt),
        items = AdvantageSystem.values().toList(),
        selected = selected,
        onDismissRequest = onDismissRequest,
        onSelect = onSelect
    ) { strategy ->
        Text(strategy.localizedName)
    }
}
