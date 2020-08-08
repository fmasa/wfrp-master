package cz.muni.fi.rpg.ui.character.skills

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.skills.adapter.SkillAdapter
import cz.muni.fi.rpg.ui.character.skills.talents.TalentsFragment
import cz.muni.fi.rpg.ui.common.CombinedLiveData
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.android.synthetic.main.fragment_character_skills.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class CharacterSkillsFragment : Fragment(R.layout.fragment_character_skills),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSkillsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)

    private val characterVm: CharacterViewModel by viewModel { parametersOf(characterId) }
    private val viewModel: SkillsViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skillList.layoutManager = NonScrollableLayoutManager(requireContext())
        val adapter = SkillAdapter(
            layoutInflater,
            { openSkillDialog(it) },
            { launch { viewModel.removeSkill(it) } }
        )
        skillList.adapter = adapter

        CombinedLiveData(viewModel.skills, characterVm.character.right())
            .observe(viewLifecycleOwner) { pair ->
                val skills = pair.first
                val stats = pair.second.getCharacteristics()

                adapter.submitList(
                    skills
                        .sortedBy { it.name }
                        .map { Pair(it, stats) }
                )

                if (skills.isNotEmpty()) {
                    skillList.visibility = View.VISIBLE
                    noSkillsIcon.visibility = View.GONE
                    noSkillsText.visibility = View.GONE
                } else {
                    skillList.visibility = View.GONE
                    noSkillsIcon.visibility = View.VISIBLE
                    noSkillsText.visibility = View.VISIBLE
                }
            }

        addSkillButton.setOnClickListener {
            openSkillDialog(null)
        }

        childFragmentManager.commit {
            replace(R.id.talentsFragment, TalentsFragment.newInstance(characterId))
        }
    }

    private fun openSkillDialog(existingSkill: Skill?) {
        SkillDialog.newInstance(characterId, existingSkill).show(childFragmentManager, null)
    }
}
