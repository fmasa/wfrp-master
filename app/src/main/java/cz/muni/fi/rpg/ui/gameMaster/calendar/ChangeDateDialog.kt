package cz.muni.fi.rpg.ui.gameMaster.calendar

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.views.ImperialCalendar
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class ChangeDateDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"
        private const val ARGUMENT_DATE = "date"

        fun newInstance(partyId: UUID, date: ImperialDate) = ChangeDateDialog().apply {
            arguments = bundleOf(
                ARGUMENT_PARTY_ID to partyId,
                ARGUMENT_DATE to date
            )
        }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val date: ImperialDate by parcelableArgument(ARGUMENT_DATE)

    private val viewModel: GameMasterViewModel by viewModel { parametersOf(partyId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val calendar = ImperialCalendar(date, requireContext())
        calendar.id = R.id.change_date_dialog_calendar

        return AlertDialog.Builder(context)
            .setView(calendar)
            .setPositiveButton(R.string.button_save) { _, _ ->
                launch {
                    viewModel.changeTime { it.copy(date = calendar.date) }
                }
            }
            .create()

    }
}