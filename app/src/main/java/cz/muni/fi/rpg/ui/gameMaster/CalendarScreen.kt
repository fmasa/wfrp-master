package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.model.domain.party.time.MannsliebPhase
import cz.muni.fi.rpg.model.domain.party.time.YearSeason
import cz.muni.fi.rpg.ui.common.composables.CardContainer

@Composable
fun CalendarScreen(
    party: Party,
    modifier: Modifier,
    onChangeTimeRequest: () -> Unit,
    onChangeDateRequest: () -> Unit
) {
    val dateTime = party.getTime()

    ScrollableColumn(modifier.background(MaterialTheme.colors.background).padding(top = 6.dp)) {
        CardContainer(Modifier.padding(horizontal = 8.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalGravity = Alignment.CenterHorizontally) {
                Time(time = dateTime.time, onChangeRequest = onChangeTimeRequest)
                Date(date = dateTime.date, onChangeRequest = onChangeDateRequest)
            }
        }
    }
}

@Composable
private fun Time(time: DateTime.TimeOfDay, onChangeRequest: () -> Unit) {
    Text(
        time.format(),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.clickable(onClick = onChangeRequest),
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

    Row(verticalGravity = Alignment.CenterVertically) {
        Icon(vectorResource(R.drawable.ic_moon), modifier = Modifier.padding(end = 4.dp))
        Text(
            stringResource(
                R.string.mannslieb_phase,
                MannsliebPhase.at(date).readableName
            )
        )
    }
}