package cz.frantisekmasa.wfrp_master.common.character.skills

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
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.AddSkillDialog
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.EditSkillDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
internal fun SkillsCard(
    characterScreenModel: CharacterScreenModel,
    skillsScreenModel: SkillsScreenModel,
    onRemove: (Skill) -> Unit,
) {
    val skills = skillsScreenModel.skills.collectWithLifecycle(null).value ?: return
    val characteristics = characterScreenModel.character
        .collectWithLifecycle(null).value?.characteristics ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            val strings = LocalStrings.current.skills

            CardTitle(strings.titleSkills)

            if (skills.isEmpty()) {
                EmptyUI(
                    text = strings.messages.characterHasNoSkills,
                    icon = Resources.Drawable.Skill,
                    size = EmptyUI.Size.Small
                )
            } else {
                var editedSkillId: Uuid? by rememberSaveable { mutableStateOf(null) }

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
                        screenModel = skillsScreenModel,
                        skillId = skillId,
                        onDismissRequest = { editedSkillId = null }
                    )
                }
            }

            var showAddSkillDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(strings.titleAdd, onClick = { showAddSkillDialog = true })

            if (showAddSkillDialog) {
                AddSkillDialog(
                    screenModel = skillsScreenModel,
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
        listOf(ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() })),
        badge = { TestNumber(skill, characteristics) }
    )
}

@Composable
private fun TestNumber(skill: Skill, characteristics: Stats) {
    val testNumber = skill.advances + skill.characteristic.characteristicValue(characteristics)

    Row {
        Text(LocalStrings.current.skills.testNumberShortcut)
        Text(testNumber.toString(), Modifier.padding(start = 4.dp))
    }
}
