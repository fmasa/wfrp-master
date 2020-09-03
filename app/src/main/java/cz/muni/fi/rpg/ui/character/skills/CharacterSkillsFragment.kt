package cz.muni.fi.rpg.ui.character.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

class CharacterSkillsFragment : Fragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSkillsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)

    private val characterVm: CharacterViewModel by viewModel { parametersOf(characterId) }
    private val skillsVm: SkillsViewModel by viewModel { parametersOf(characterId) }
    private val talentsVm: TalentsViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme {
                    ScrollableColumn(Modifier.background(MaterialTheme.colors.background)) {
                        SkillsCard(
                            characterVm,
                            skillsVm,
                            onClick = { openSkillDialog(it) },
                            onRemove = { launch { skillsVm.removeSkill(it) } },
                            onNewSkillButtonClicked = { openSkillDialog(null) },
                        )

                        TalentsCard(
                            talentsVm,
                            onClick = { openTalentDialog(it) },
                            onRemove = { launch { talentsVm.removeTalent(it) } },
                            onAddButtonClicked = { openTalentDialog(null) }
                        )
                    }
                }
            }
        }
    }

    private fun openSkillDialog(existingSkill: Skill?) {
        SkillDialog.newInstance(characterId, existingSkill).show(childFragmentManager, null)
    }

    private fun openTalentDialog(existingTalent: Talent?) {
        val dialog = TalentDialog.newInstance(existingTalent)
        dialog.setOnSuccessListener { talent ->
            launch {
                talentsVm.saveTalent(talent)

                withContext(Dispatchers.Main) { dialog.dismiss() }
            }
        }.show(childFragmentManager, "TalentDialog")
    }
}
