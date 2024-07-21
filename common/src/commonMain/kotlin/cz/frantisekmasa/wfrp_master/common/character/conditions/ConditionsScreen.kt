package cz.frantisekmasa.wfrp_master.common.character.conditions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalEntryScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Condition
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch

@Composable
fun ConditionsScreen(
    state: ConditionsScreenState,
    updateConditions: (CurrentConditions) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConditionsForm(
        modifier = modifier,
        conditions = state.conditions,
        onUpdate = updateConditions,
        conditionsJournal = state.conditionsJournal,
    )
}

@Composable
fun ConditionsForm(
    conditions: CurrentConditions,
    conditionsJournal: ConditionsJournal,
    onUpdate: (CurrentConditions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        LazyColumn(
            modifier.background(MaterialTheme.colors.surface),
            contentPadding = PaddingValues(top = Spacing.small),
        ) {
            items(Condition.entries, key = { it }) { condition ->
                Column {
                    ConditionRow(
                        condition = condition,
                        conditionsJournal = conditionsJournal,
                        state = conditions,
                        update = {
                            Reporting.record { conditionsChanged() }
                            onUpdate(it)
                        },
                        snackbarHostState = scaffoldState.snackbarHostState,
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun ConditionRow(
    condition: Condition,
    conditionsJournal: ConditionsJournal,
    state: CurrentConditions,
    update: (CurrentConditions) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val journalEntry = conditionsJournal.entries.getValue(condition)
        val navigation = LocalNavigationTransaction.current
        val notFoundMessage =
            stringResource(Str.journal_messages_entry_not_found, journalEntry.journalEntryName)
        val coroutineScope = rememberCoroutineScope()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.clickable {
                    if (journalEntry.journalEntryId != null) {
                        Reporting.record { journalOpened("conditions") }
                        navigation.navigate(
                            JournalEntryScreen(
                                journalEntry.partyId,
                                journalEntry.journalEntryId,
                            ),
                        )
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(notFoundMessage)
                        }
                    }
                }
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(),
        ) {
            ConditionIcon(condition)
            Text(
                condition.localizedName,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
            )
        }

        val count = state.count(condition)

        if (condition.isStackable()) {
            NumberPicker(
                value = count,
                onIncrement = { update(state.addConditions(condition)) },
                onDecrement = { update(state.removeCondition(condition)) },
                modifier = Modifier.padding(vertical = Spacing.extraTiny),
            )
        } else {
            Switch(
                modifier = Modifier.padding(horizontal = 24.dp),
                checked = count != 0,
                onCheckedChange = { checked ->
                    update(
                        if (checked) {
                            state.addConditions(condition)
                        } else {
                            state.removeCondition(condition)
                        },
                    )
                },
            )
        }
    }
}

@Composable
fun ConditionIcon(
    condition: Condition,
    size: Dp = 28.dp,
) {
    val iconRes =
        when (condition) {
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
        Modifier.size(size),
    )
}

data class ConditionsJournal(
    val entries: ImmutableMap<Condition, Entry>,
) {
    data class Entry(
        val partyId: PartyId,
        val journalEntryId: Uuid?,
        val journalEntryName: String,
    )
}
