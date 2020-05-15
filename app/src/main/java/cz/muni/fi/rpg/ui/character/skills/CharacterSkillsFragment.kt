package cz.muni.fi.rpg.ui.character.skills

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.character.skills.adapter.SkillAdapter
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_character_skills.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterSkillsFragment : Fragment(R.layout.fragment_character_skills),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val viewModel: CharacterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skillList.layoutManager = LinearLayoutManager(context)
        val adapter = SkillAdapter(layoutInflater) { openSkillDialog(it) }
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
