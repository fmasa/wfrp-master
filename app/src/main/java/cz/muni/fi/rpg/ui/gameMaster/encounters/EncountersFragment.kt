package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.gameMaster.encounters.adapter.EncounterAdapter
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import kotlinx.android.synthetic.main.fragment_encounters.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class EncountersFragment : Fragment(R.layout.fragment_encounters) {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = EncountersFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
            }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EncounterAdapter(layoutInflater) {}
        encounterListRecycler.adapter = adapter
        encounterListRecycler.layoutManager = LinearLayoutManager(context)

        viewModel.encounters.observe(viewLifecycleOwner) { encounters ->
            progress.toggleVisibility(false)
            mainView.toggleVisibility(true)

            adapter.submitList(encounters)

            noEncountersText.toggleVisibility(encounters.isEmpty())
            noEncountersIcon.toggleVisibility(encounters.isEmpty())
            encounterListRecycler.toggleVisibility(encounters.isNotEmpty())
        }

        addEncounter.setOnClickListener {
            EncounterDialog.newInstance(partyId, null).show(childFragmentManager, null)
        }
    }
}