package cz.frantisekmasa.wfrp_master.common.combat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.ui.StatBlock
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FlowRow
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.encounters.CombatantItem
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CombatantSheet(
    combatant: CombatantItem,
    viewModel: CombatScreenModel,
    isGroupAdvantageSystemEnabled: Boolean,
    advantageCap: Advantage,
    onRemoveRequest: (() -> Unit)?,
    onDetailOpenRequest: () -> Unit,
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.large),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                combatant.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.clickable(onClick = onDetailOpenRequest)
            )

            if (onRemoveRequest != null) {
                var contextMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { contextMenuExpanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            stringResource(Str.common_ui_label_open_context_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = contextMenuExpanded,
                        onDismissRequest = { contextMenuExpanded = false },
                    ) {
                        DropdownMenuItem(onClick = onRemoveRequest) {
                            Text(stringResource(Str.combat_button_remove_combatant))
                        }
                    }
                }
            }
        }

        ConditionsBox(
            modifier = Modifier.padding(bottom = Spacing.small),
            combatant = combatant,
            screenModel = viewModel,
        )

        StatBlock(
            combatant.characterId,
            combatant.characteristics,
            rememberSaveable(combatant.combatant.id) {
                viewModel.getStatBlockData(combatant.characterId)
            },
        )

        Divider()

        Row(Modifier.padding(bottom = Spacing.medium)) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                CombatantWounds(combatant, viewModel)
            }

            if (!isGroupAdvantageSystemEnabled) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                    CombatantAdvantage(combatant, viewModel, advantageCap)
                }
            }
        }
    }
}

@Composable
private fun CombatantWounds(combatant: CombatantItem, viewModel: CombatScreenModel) {
    val coroutineScope = rememberCoroutineScope()
    val updateWounds = { wounds: Wounds ->
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.updateWounds(combatant, wounds)
        }
    }

    val wounds = combatant.wounds

    NumberPicker(
        label = stringResource(Str.points_wounds),
        value = wounds.current,
        onIncrement = { updateWounds(wounds.restore(1)) },
        onDecrement = { updateWounds(wounds.lose(1)) },
    )
}

@Composable
private fun CombatantAdvantage(
    combatant: CombatantItem,
    viewModel: CombatScreenModel,
    advantageCap: Advantage,
) {
    val coroutineScope = rememberCoroutineScope()
    val updateAdvantage = { advantage: Advantage ->
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.updateAdvantage(combatant.combatant, advantage)
        }
    }

    val advantage = combatant.combatant.advantage

    AdvantagePicker(
        label = stringResource(Str.combat_label_advantage),
        value = advantage,
        onChange = { updateAdvantage(it.coerceAtMost(advantageCap)) }
    )
}

@Composable
private fun ConditionsBox(
    modifier: Modifier,
    combatant: CombatantItem,
    screenModel: CombatScreenModel,
) {
    var conditionsDialogOpened by remember { mutableStateOf(false) }

    if (conditionsDialogOpened) {
        ConditionsDialog(
            combatantItem = combatant,
            screenModel = screenModel,
            onDismissRequest = { conditionsDialogOpened = false },
        )
    }

    Box(
        modifier = modifier.clickable { conditionsDialogOpened = true }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val conditions = combatant.conditions

        if (conditions.areEmpty()) {
            Text(stringResource(Str.combat_messages_no_conditions))
        } else {
            FlowRow(verticalSpacing = Spacing.small, horizontalSpacing = Spacing.small) {
                conditions.toList().forEach { (condition, count) ->
                    repeat(count) { index ->
                        key(condition, index) {
                            ConditionIcon(condition, size = 28.dp)
                        }
                    }
                }
            }
        }
    }
}
