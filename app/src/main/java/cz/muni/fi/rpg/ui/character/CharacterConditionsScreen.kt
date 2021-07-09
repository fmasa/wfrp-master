package cz.muni.fi.rpg.ui.character

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Condition
import cz.frantisekmasa.wfrp_master.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias ConditionTransformation = (CurrentConditions) -> CurrentConditions

@Composable
internal fun CharacterConditionsScreen(
    character: Character,
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val conditions = character.getConditions()
    val updateConditions = { transformation: ConditionTransformation ->
        val newConditions = transformation(conditions)

        if (newConditions != conditions) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.update { it.updateConditions(newConditions) }
                }
            }
        }
    }

    Column(
        modifier
            .background(MaterialTheme.colors.surface)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small)
    ) {
        Condition.values().forEach { condition ->
            ConditionRow(condition, conditions.count(condition), updateConditions)
            Divider()
        }
    }
}

@Composable
private fun ConditionRow(
    condition: Condition,
    count: Int,
    update: (ConditionTransformation) -> Unit
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
            conditionName(condition),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )

        if (condition.isStackable()) {
            NumberPicker(
                value = count,
                onIncrement = { update { it.addConditions(condition) } },
                onDecrement = { update { it.removeCondition(condition) } }
            )
        } else {
            Switch(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                checked = count != 0,
                onCheckedChange = { checked ->
                    update {
                        if (checked) it.addConditions(condition)
                        else it.removeCondition(condition)
                    }
                }
            )
        }
    }
}

@Composable
private fun ConditionIcon(condition: Condition) {
    val iconRes = when (condition) {
        Condition.ABLAZE -> R.drawable.ic_condition_ablaze
        Condition.BLINDED -> R.drawable.ic_condition_blinded
        Condition.BROKEN -> R.drawable.ic_condition_broken
        Condition.DEAFENED -> R.drawable.ic_condition_deafened
        Condition.ENTANGLED -> R.drawable.ic_condition_entangled
        Condition.FATIGUED -> R.drawable.ic_condition_fatigued
        Condition.POISONED -> R.drawable.ic_condition_poisoned
        Condition.PRONE -> R.drawable.ic_condition_prone
        Condition.STUNNED -> R.drawable.ic_condition_stunned
        Condition.SURPRISED -> R.drawable.ic_condition_surprised
        Condition.UNCONSCIOUS -> R.drawable.ic_condition_unconscious
        Condition.BLEEDING -> R.drawable.ic_condition_bleeding
    }
    Image(
        painterResource(iconRes),
        conditionName(condition),
        Modifier.size(28.dp)
    )
}

@Composable
private fun conditionName(condition: Condition): String {
    return condition.name.toLowerCase(Locale.current).capitalize(Locale.current)
}
