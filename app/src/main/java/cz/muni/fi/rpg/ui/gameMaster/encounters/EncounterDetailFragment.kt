package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EncounterDetailFragment : BaseFragment(R.layout.fragment_encounter_detail) {

    private val args: EncounterDetailFragmentArgs by navArgs()

    private val viewModel: EncounterDetailViewModel by viewModel { parametersOf(args.encounterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.party.right().observe(viewLifecycleOwner) { party ->
            setSubtitle(party.getName())
        }

        viewModel.encounter.right().observe(viewLifecycleOwner) { encounter ->
            setTitle(encounter.name)
        }
    }
}