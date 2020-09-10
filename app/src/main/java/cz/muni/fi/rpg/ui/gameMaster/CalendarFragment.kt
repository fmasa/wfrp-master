package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.model.domain.party.time.MannsliebPhase
import cz.muni.fi.rpg.model.domain.party.time.YearSeason
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.gameMaster.calendar.ChangeDateDialog
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class CalendarFragment : Fragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default), TimePickerDialog.OnTimeSetListener {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = CalendarFragment().apply {
            arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
        }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: GameMasterViewModel by viewModel { parametersOf(partyId) }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        launch {
            viewModel.changeTime { it.withTime(DateTime.TimeOfDay(hourOfDay, minute)) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                val dateTime = viewModel.party.right().observeAsState().value?.getTime()
                    ?: return@Theme
                CalendarScreen(
                    dateTime,
                    onChangeTimeRequest = {
                        val time = dateTime.time
                        TimePickerDialog.newInstance(
                            this@CalendarFragment,
                            time.hour,
                            time.minute,
                            true
                        ).show(childFragmentManager, "TimePickerDialog")
                    },
                    onChangeDateRequest = {
                        ChangeDateDialog.newInstance(partyId, dateTime.date)
                            .show(childFragmentManager, null)
                    },
                )
            }
        }
    }
}

@Composable
private fun CalendarScreen(
    dateTime: DateTime,
    onChangeTimeRequest: () -> Unit,
    onChangeDateRequest: () -> Unit
) {
    ScrollableColumn(Modifier.background(MaterialTheme.colors.background).padding(top = 6.dp)) {
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