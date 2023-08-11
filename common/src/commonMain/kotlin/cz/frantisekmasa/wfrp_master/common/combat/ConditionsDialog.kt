package cz.frantisekmasa.wfrp_master.common.combat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsForm
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.encounters.CombatantItem
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ConditionsDialog(
    combatantItem: CombatantItem,
    screenModel: CombatScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val conditions = combatantItem.conditions
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onDismissRequest) },
                    title = { Text(stringResource(Str.character_tab_conditions)) },
                )
            }
        ) {
            ConditionsForm(
                modifier = Modifier.fillMaxSize(),
                conditions = conditions,
                onUpdate = { newConditions ->
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            screenModel.updateConditions(combatantItem, newConditions)
                        }
                    }
                }
            )
        }
    }
}
