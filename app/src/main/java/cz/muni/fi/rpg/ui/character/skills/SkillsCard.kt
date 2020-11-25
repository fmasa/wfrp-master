package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.skills.dialog.AddSkillDialog
import cz.muni.fi.rpg.ui.character.skills.dialog.EditSkillDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel

@Composable
internal fun SkillsCard(
    characterVm: CharacterViewModel,
    skillsVm: SkillsViewModel,
    onRemove: (Skill) -> Unit,
) {
    val skills = skillsVm.skills.collectAsState(null).value ?: return
    val characteristics = characterVm.character.right()
        .collectAsState(null).value?.getCharacteristics() ?: return

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
                var editedSkill: Skill? by savedInstanceState { null }

                Column {
                    for (skill in skills) {
                        SkillItem(
                            skill,
                            characteristics,
                            onClick = { editedSkill = skill },
                            onRemove = { onRemove(skill) },
                        )
                    }
                }

                editedSkill?.let { skill ->
                    EditSkillDialog(
                        viewModel = skillsVm,
                        skill = skill,
                        onDismissRequest = { editedSkill = null }
                    )
                }
            }

            var showAddSkillDialog by savedInstanceState { false }

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
        skill.characteristic.getIconId(),
        onClick = onClick,
        listOf(ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove() })),
        badgeContent = { TestNumber(skill, characteristics) }
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
