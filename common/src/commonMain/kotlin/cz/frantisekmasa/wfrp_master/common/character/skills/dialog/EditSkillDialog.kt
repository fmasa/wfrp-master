package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditSkillDialog(
    screenModel: SkillsScreenModel,
    skillId: Uuid,
    onDismissRequest: () -> Unit
) {
    val skill = screenModel.skills.collectWithLifecycle(null).value?.firstOrNull { it.id == skillId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (skill.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            SkillDetail(
                skill,
                onDismissRequest = onDismissRequest,
                onAdvancesChange = { advances ->
                    coroutineScope.launch(Dispatchers.IO) {
                        screenModel.saveSkill(skill.copy(advances = advances))
                    }
                }
            )
        } else {
            NonCompendiumSkillForm(
                screenModel = screenModel,
                existingSkill = skill,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}
