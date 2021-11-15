package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.character.skills.dialog.AddSkillDialog
import cz.muni.fi.rpg.ui.character.skills.dialog.EditSkillDialog
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import java.util.UUID

@Composable
internal fun SkillsCard(
    characterVm: CharacterViewModel,
    skillsVm: SkillsViewModel,
    onRemove: (Skill) -> Unit,
) {
    val skills = skillsVm.skills.collectWithLifecycle(null).value ?: return
    val characteristics = characterVm.character
        .collectWithLifecycle(null).value?.getCharacteristics() ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_skills)

            if (skills.isEmpty()) {
                EmptyUI(
                    R.string.no_skills,
                    R.drawable.ic_skills,
                    size = EmptyUI.Size.Small
                )
            } else {
                var editedSkillId: UUID? by rememberSaveable { mutableStateOf(null) }

                for (skill in skills) {
                    SkillItem(
                        skill,
                        characteristics,
                        onClick = { editedSkillId = skill.id },
                        onRemove = { onRemove(skill) },
                    )
                }

                editedSkillId?.let { skillId ->
                    EditSkillDialog(
                        viewModel = skillsVm,
                        skillId = skillId,
                        onDismissRequest = { editedSkillId = null }
                    )
                }
            }

            var showAddSkillDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(R.string.title_addSkill, onClick = { showAddSkillDialog = true })

            if (showAddSkillDialog) {
                AddSkillDialog(
                    viewModel = skillsVm,
                    onDismissRequest = { showAddSkillDialog = false }
                )
            }
        }
    }
}

@Composable
private fun SkillItem(
    skill: Skill,
    characteristics: Stats,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    CardItem(
        skill.name,
        skill.description,
        icon = { ItemIcon(skill.characteristic.getIcon(), ItemIcon.Size.Small) } ,
        onClick = onClick,
        listOf(ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove() })),
        badge = { TestNumber(skill, characteristics) }
    )
}

@Composable
private fun TestNumber(skill: Skill, characteristics: Stats) {
    val testNumber = skill.advances + skill.characteristic.characteristicValue(characteristics)

    Row {
        Text(stringResource(R.string.skill_test_number_shortcut))
        Text(testNumber.toString(), Modifier.padding(start = 4.dp))
    }
}
