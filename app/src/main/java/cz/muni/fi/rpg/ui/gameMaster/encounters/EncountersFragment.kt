package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncountersFragment : Fragment(R.layout.fragment_encounters) {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = EncountersFragment ()
            .apply {
            arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
        }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }
}