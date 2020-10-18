package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.datepicker
import com.vanpra.composematerialdialogs.datetime.timepicker
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.model.domain.party.time.MannsliebPhase
import cz.muni.fi.rpg.model.domain.party.time.YearSeason
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import timber.log.Timber
import java.time.LocalTime

@Composable
internal fun CalendarScreen(
    party: Party,
    viewModel: GameMasterViewModel,
    modifier: Modifier,
    onChangeDateRequest: () -> Unit
) {
    val dateTime = party.getTime()

    ScrollableColumn(modifier.background(MaterialTheme.colors.background).padding(top = 6.dp)) {
        CardContainer(Modifier.padding(horizontal = 8.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Time(
                    viewModel = viewModel,
                    time = dateTime.time
                )
                Date(date = dateTime.date, onChangeRequest = onChangeDateRequest)
            }
        }
    }
}

@Composable
private fun Time(viewModel: GameMasterViewModel, time: DateTime.TimeOfDay) {
    val dialog = MaterialDialog()

    dialog.build {
        timepicker(
            initialTime = LocalTime.of(time.hour, time.minute),
            onCancel = { dialog.hide() },
            onComplete = { newTime ->
                viewModel.changeTime {
                    it.withTime(DateTime.TimeOfDay(newTime.hour, newTime.minute))
                }
                dialog.hide()
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
private fun Date(date: ImperialDate, onChangeRequest: () -> Unit) {
    Text(
        date.format(),
        style = MaterialTheme.typography.h6,
        modifier = Modifier.clickable(onClick = onChangeRequest),
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