package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.frantisekmasa.wfrp_master.core.ui.buttons.SaveTextButton
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.core.domain.time.ImperialDate
import cz.frantisekmasa.wfrp_master.core.domain.time.MannsliebPhase
import cz.frantisekmasa.wfrp_master.core.domain.time.YearSeason
import cz.frantisekmasa.wfrp_master.core.ui.components.TimePickerLayout
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.muni.fi.rpg.ui.gameMaster.calendar.ImperialCalendar
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun CalendarScreen(
    party: Party,
    viewModel: GameMasterViewModel,
    modifier: Modifier,
) {
    val dateTime = party.getTime()

    Column(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 6.dp)
    ) {
        CardContainer(Modifier.padding(horizontal = 8.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Time(
                    viewModel = viewModel,
                    time = dateTime.time,
                )
                Date(
                    viewModel = viewModel,
                    date = dateTime.date,
                )
            }
        }
    }
}

@Composable
private fun Time(viewModel: GameMasterViewModel, time: DateTime.TimeOfDay) {
    var dialogOpened by savedInstanceState { false }

    if (dialogOpened) {
        TimePickerDialog(
            viewModel = viewModel,
            default = time,
            onDismissRequest = { dialogOpened = false },
        )
    }

    Text(
        time.format(),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.clickable(onClick = { dialogOpened = true }),
    )
}

@Composable
private fun TimePickerDialog(
    viewModel: GameMasterViewModel,
    default: DateTime.TimeOfDay,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .padding(horizontal = Spacing.extraLarge),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        stringResource(R.string.title_select_time),
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.h6,
                    )
                }

                val value = savedInstanceState { default }

                TimePickerLayout(selectedTime = value)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.medium, bottom = Spacing.small, end = Spacing.small),
                    horizontalArrangement = Arrangement.End,
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    TextButton(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.changeTime {
                                    it.withTime(value.value)
                                }

                                withContext(Dispatchers.Main) { onDismissRequest() }
                            }
                        },
                    ) {
                        Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
                    }
                }
            }
        }
    }
}

@Composable
private fun Date(viewModel: GameMasterViewModel, date: ImperialDate) {
    var dialogVisible by savedInstanceState { false }

    if (dialogVisible) {
        Dialog(
            onDismissRequest = { dialogVisible = false }, properties = null
        ) {
            Surface(shape = MaterialTheme.shapes.large) {
                var selectedDate by savedInstanceState { date }

                Column {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        ImperialCalendar(
                            date = selectedDate,
                            onDateChange = { selectedDate = it },
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        val coroutineScope = rememberCoroutineScope()
                        SaveTextButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.changeTime { it.copy(date = selectedDate) }
                                dialogVisible = false
                            }
                        })
                    }
                }
            }
        }
    }

    Text(
        date.format(),
        modifier = Modifier.clickable(onClick = { dialogVisible = true }),
        style = MaterialTheme.typography.h6
    )
    Text(YearSeason.at(date).readableName, modifier = Modifier.padding(top = 8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            vectorResource(R.drawable.ic_moon),
            VisualOnlyIconDescription,
            Modifier.padding(end = 4.dp),
        )
        Text(
            stringResource(
                R.string.mannslieb_phase,
                MannsliebPhase.at(date).readableName
            )
        )
    }
}