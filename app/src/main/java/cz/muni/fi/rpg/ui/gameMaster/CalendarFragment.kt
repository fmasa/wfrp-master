package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.domain.party.time.MannsliebPhase
import cz.muni.fi.rpg.model.domain.party.time.YearSeason
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.gameMaster.calendar.ChangeDateDialog
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar),
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.party.right().observe(viewLifecycleOwner) { party ->
            party.getTime().let {
                time.text = it.time.format()
                date.text = it.date.format()
                yearSeason.text = YearSeason.at(it.date).readableName
                moonPhase.text =
                    getString(R.string.mannslieb_phase, MannsliebPhase.at(it.date).readableName)
            }

            date.setOnClickListener {
                ChangeDateDialog.newInstance(partyId, party.getTime().date)
                    .show(childFragmentManager, null)
            }

            time.setOnClickListener {
                val time = party.getTime().time
                TimePickerDialog.newInstance(this, time.hour, time.minute, true)
                    .show(childFragmentManager, "TimePickerDialog")
            }
        }
    }
}