package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SkillsViewModel

@Composable
fun EditSkillDialog(
    viewModel: SkillsViewModel,
    skill: Skill,
    onDismissRequest: () -> Unit
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (skill.compendiumId != null) {
            AdvancesForm(
                existingSkill = skill,
                compendiumSkillId = skill.compendiumId,
                viewModel = viewModel,
                onComplete = onDismissRequest
            )
        } else {
            NonCompendiumSkillForm(
                viewModel = viewModel,
                existingSkill = skill,
                onComplete = onDismissRequest
            )
        }
    }
}