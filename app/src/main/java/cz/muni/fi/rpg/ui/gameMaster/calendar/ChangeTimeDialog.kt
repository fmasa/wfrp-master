package cz.muni.fi.rpg.ui.gameMaster.calendar
//
//import android.app.Dialog
//import android.app.TimePickerDialog
//import android.os.Bundle
//import androidx.appcompat.app.AlertDialog
//import androidx.core.os.bundleOf
//import androidx.fragment.app.DialogFragment
//import cz.muni.fi.rpg.R
//import cz.muni.fi.rpg.model.domain.party.time.DateTime
//import cz.muni.fi.rpg.ui.common.parcelableArgument
//import cz.muni.fi.rpg.ui.common.serializableArgument
//import cz.muni.fi.rpg.ui.views.ImperialCalendar
//import cz.muni.fi.rpg.viewModels.GameMasterViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.koin.android.viewmodel.ext.android.viewModel
//import org.koin.core.parameter.parametersOf
//import java.util.*
//
//class ChangeTimeDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
//
//    companion object {
//        private const val ARGUMENT_PARTY_ID = "partyId"
//        private const val ARGUMENT_TIME = "date"
//
//        fun newInstance(partyId: UUID, date: DateTime.TimeOfDay) = ChangeDateDialog().apply {
//            arguments = bundleOf(
//                ARGUMENT_PARTY_ID to partyId,
//                ARGUMENT_TIME to date
//            )
//        }
//    }
//
//    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
//    private val time: DateTime.TimeOfDay by parcelableArgument(ARGUMENT_TIME)
//
//    private val viewModel: GameMasterViewModel by viewModel { parametersOf(partyId) }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val context = requireContext()
//
//        val timePicker = TimePickerDialog(time, requireContext())
//        return AlertDialog.Builder(context)
//            .setView(timePicker)
//            .setPositiveButton(R.string.button_save) { _, _ ->
//                launch {
//                    viewModel.changeTime { it.copy(date = timePicker.date) }
//                }
//            }
//            .create()
//    }
//}

//<TimePicker android:id="@+id/timePicker1"
//android:layout_width="wrap_content"
//android:layout_height="wrap_content"
//android:timePickerMode="clock" />
