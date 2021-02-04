package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
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
                onDismissRequest = onDismissRequest
            )
        } else {
            NonCompendiumSkillForm(
                viewModel = viewModel,
                existingSkill = skill,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}