package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditSkillDialog(
    screenModel: SkillsScreenModel,
    skillId: Uuid,
    onDismissRequest: () -> Unit
) {
    val skill = screenModel.items.collectWithLifecycle(null).value?.firstOrNull { it.id == skillId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (skill.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            SkillDetail(
                skill,
                onDismissRequest = onDismissRequest,
                subheadBar = {
                    AdvancesBar(
                        advances = skill.advances,
                        minAdvances = if (skill.advanced) 1 else 0,
                        onAdvancesChange = { advances ->
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.saveSkill(skill.copy(advances = advances))
                            }
                        },
                    )
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

@Composable
private fun AdvancesBar(
    advances: Int,
    minAdvances: Int,
    onAdvancesChange: (advances: Int) -> Unit
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(LocalStrings.current.skills.labelAdvances)
            NumberPicker(
                value = advances,
                onIncrement = { onAdvancesChange(advances + 1) },
                onDecrement = {
                    if (advances > minAdvances) {
                        onAdvancesChange(advances - 1)
                    }
                }
            )
        }
    }
}
