package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditSkillDialog(
    viewModel: SkillsViewModel,
    skillId: UUID,
    onDismissRequest: () -> Unit
) {
    val skill = viewModel.skills.observeAsState().value?.firstOrNull { it.id == skillId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (skill.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            SkillDetail(
                skill,
                onDismissRequest = onDismissRequest,
                onAdvancesChange = { advances ->
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.saveSkill(skill.copy(advances = advances))
                    }
                }
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
