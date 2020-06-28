package cz.muni.fi.rpg.ui.character.skills.talents

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.android.synthetic.main.fragment_talents.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TalentsFragment : Fragment(R.layout.fragment_talents),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = TalentsFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
            }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: TalentsViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        talentList.layoutManager = NonScrollableLayoutManager(requireContext())
        val adapter = TalentAdapter(
            layoutInflater,
            { openTalentDialog(it) },
            { launch { viewModel.removeTalent(it) } }
        )
        talentList.adapter = adapter

        viewModel.talents.observe(viewLifecycleOwner) { talents ->
            adapter.submitList(talents.sortedBy { it.name })
        }

        addTalentButton.setOnClickListener {
            openTalentDialog(null)
        }
    }

    private fun openTalentDialog(existingTalent: Talent?) {
        val dialog = TalentDialog.newInstance(existingTalent)
        dialog.setOnSuccessListener { talent ->
            launch {
                viewModel.saveTalent(talent)

                withContext(Dispatchers.Main) { dialog.dismiss() }
            }
        }.show(childFragmentManager, "TalentDialog")
    }
}