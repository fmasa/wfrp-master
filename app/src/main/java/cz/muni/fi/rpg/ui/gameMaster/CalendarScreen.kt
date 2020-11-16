package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.timepicker
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.model.domain.party.time.MannsliebPhase
import cz.muni.fi.rpg.model.domain.party.time.YearSeason
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.gameMaster.calendar.ImperialCalendar
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

@Composable
internal fun CalendarScreen(
    party: Party,
    viewModel: GameMasterViewModel,
    modifier: Modifier,
) {
    val dateTime = party.getTime()

    ScrollableColumn(modifier.background(MaterialTheme.colors.background).padding(top = 6.dp)) {
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
    val dialog = MaterialDialog()
    val coroutineScope = rememberCoroutineScope()

    dialog.build {
        timepicker(
            initialTime = LocalTime.of(time.hour, time.minute),
            onCancel = { dialog.hide() },
            onComplete = { newTime ->
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.changeTime {
                        it.withTime(DateTime.TimeOfDay(newTime.hour, newTime.minute))
                    }

                    withContext(Dispatchers.Main) { dialog.hide() }
                }
            }
        )
    }

    Text(
        time.format(),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.clickable(onClick = { dialog.show() }),
    )
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
                        Modifier.fillMaxWidth().padding(bottom = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        val coroutineScope = rememberCoroutineScope()
                        TextButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.changeTime { it.copy(date = selectedDate) }
                                dialogVisible = false
                            }
                        }) {
                            Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
                        }
                    }
                }
            }
        }
    }

    Text(
        date.format(),
        style = MaterialTheme.typography.h6,
        modifier = Modifier.clickable(onClick = { dialogVisible = true }),
    )
    Text(YearSeason.at(date).readableName, modifier = Modifier.padding(top = 8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(vectorResource(R.drawable.ic_moon), modifier = Modifier.padding(end = 4.dp))
        Text(
            stringResource(
                R.string.mannslieb_phase,
                MannsliebPhase.at(date).readableName
            )
        )
    }
}