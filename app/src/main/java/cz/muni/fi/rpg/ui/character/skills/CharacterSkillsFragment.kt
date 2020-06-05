package cz.muni.fi.rpg.ui.character.skills

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.character.skills.adapter.SkillAdapter
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.android.synthetic.main.fragment_character_skills.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

class CharacterSkillsFragment : Fragment(R.layout.fragment_character_skills),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSkillsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val viewModel: SkillsViewModel by viewModel {
        parametersOf(arguments?.getParcelable(ARGUMENT_CHARACTER_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skillList.layoutManager = LinearLayoutManager(context)
        val adapter = SkillAdapter(
            layoutInflater,
            { openSkillDialog(it) },
            { launch {viewModel.removeSkill(it) }}
        )
        skillList.adapter = adapter

        viewModel.skills.observe(viewLifecycleOwner, Observer { skills ->
            adapter.submitList(skills.sortedBy { it.name })

            if (skills.isNotEmpty()) {
                skillList.visibility = View.VISIBLE
                noSkillsIcon.visibility = View.GONE
                noSkillsText.visibility = View.GONE
            } else {
                skillList.visibility = View.GONE
                noSkillsIcon.visibility = View.VISIBLE
                noSkillsText.visibility = View.VISIBLE
            }
        })

        addSkillButton.setOnClickListener {
            openSkillDialog(null)
        }
    }

    private fun openSkillDialog(existingSkill: Skill?) {
        val dialog = SkillDialog.newInstance(existingSkill)
        dialog.setOnSuccessListener { skill ->
            launch {
                viewModel.saveSkill(skill)

                withContext(Dispatchers.Main) { dialog.dismiss() }
            }
        }.show(childFragmentManager, "SkillDialog")
    }
}
