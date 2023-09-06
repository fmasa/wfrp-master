package cz.frantisekmasa.wfrp_master.common.character.conditions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Condition
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ConditionsScreen(
    character: Character,
    screenModel: CharacterScreenModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val conditions = character.conditions

    ConditionsForm(
        modifier = modifier,
        conditions = conditions,
        onUpdate = { newConditions ->
            if (newConditions != conditions) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        screenModel.update { it.updateConditions(newConditions) }
                    }
                }
            }
        }
    )
}

@Composable
fun ConditionsForm(
    conditions: CurrentConditions,
    onUpdate: (CurrentConditions) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .background(MaterialTheme.colors.surface)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small)
    ) {
        Condition.values().forEach { condition ->
            ConditionRow(condition, conditions, onUpdate)
            Divider()
        }
    }
}

@Composable
private fun ConditionRow(
    condition: Condition,
    state: CurrentConditions,
    update: (CurrentConditions) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 2.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConditionIcon(condition)
        Text(
            condition.localizedName,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )

        val count = state.count(condition)

        if (condition.isStackable()) {
            NumberPicker(
                value = count,
                onIncrement = { update(state.addConditions(condition)) },
                onDecrement = { update(state.removeCondition(condition)) },
            )
        } else {
            Switch(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                checked = count != 0,
                onCheckedChange = { checked ->
                    update(
                        if (checked)
                            state.addConditions(condition)
                        else state.removeCondition(condition)
                    )
                }
            )
        }
    }
}

@Composable
fun ConditionIcon(condition: Condition, size: Dp = 28.dp) {
    val iconRes = when (condition) {
        Condition.ABLAZE -> Resources.Drawable.ConditionAblaze
        Condition.BLINDED -> Resources.Drawable.ConditionBlinded
        Condition.BROKEN -> Resources.Drawable.ConditionBroken
        Condition.DEAFENED -> Resources.Drawable.ConditionDeafened
        Condition.ENTANGLED -> Resources.Drawable.ConditionEntangled
        Condition.FATIGUED -> Resources.Drawable.ConditionFatigued
        Condition.POISONED -> Resources.Drawable.ConditionPoisoned
        Condition.PRONE -> Resources.Drawable.ConditionProne
        Condition.STUNNED -> Resources.Drawable.ConditionStunned
        Condition.SURPRISED -> Resources.Drawable.ConditionSurprised
        Condition.UNCONSCIOUS -> Resources.Drawable.ConditionUnconscious
        Condition.BLEEDING -> Resources.Drawable.ConditionBleeding
    }
    Image(
        drawableResource(iconRes),
        condition.localizedName,
        Modifier.size(size)
    )
}
