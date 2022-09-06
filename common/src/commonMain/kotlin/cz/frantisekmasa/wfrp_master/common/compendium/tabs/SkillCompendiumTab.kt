package cz.frantisekmasa.wfrp_master.common.compendium.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.checkboxValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SkillCompendiumTab(screenModel: CompendiumScreenModel, width: Dp) {
    val messages = LocalStrings.current.skills.messages

    CompendiumTab(
        liveItems = screenModel.skills,
        emptyUI = {
            EmptyUI(
                text = messages.noSkillsInCompendium,
                subText = messages.noSkillsInCompendiumSubtext,
                icon = Resources.Drawable.Skill,
            )
        },
        remover = screenModel::remove,
        saver = screenModel::save,
        dialog = { SkillDialog(it, screenModel) },
        width = width,
    ) { skill ->
        ListItem(
            icon = { ItemIcon(skill.characteristic.getIcon()) },
            text = { Text(skill.name) }
        )
        Divider()
    }
}

interface CompendiumItemFormData<T : CompendiumItem<T>> : HydratedFormData<T>

@Stable
private data class SkillFormData(
    val id: Uuid,
    val name: InputValue,
    val description: InputValue,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
) : CompendiumItemFormData<Skill> {
    companion object {
        @Composable
        fun fromItem(item: Skill?) = SkillFormData(
            id = remember { item?.id ?: uuid4() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            description = inputValue(item?.description ?: ""),
            characteristic = rememberSaveable { mutableStateOf(item?.characteristic ?: Characteristic.AGILITY) },
            advanced = checkboxValue(item?.advanced ?: false),
        )
    }

    override fun toValue() = Skill(
        id = id,
        name = name.value,
        description = description.value,
        characteristic = characteristic.value,
        advanced = advanced.value
    )

    override fun isValid() =
        name.isValid() &&
            name.value.length <= Skill.NAME_MAX_LENGTH &&
            description.value.length <= Skill.DESCRIPTION_MAX_LENGTH
}

@Composable
private fun SkillDialog(
    dialogState: MutableState<DialogState<Skill?>>,
    screenModel: CompendiumScreenModel,
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val formData = SkillFormData.fromItem(dialogStateValue.item)
    val strings = LocalStrings.current.skills

    CompendiumItemDialog(
        title = if (dialogStateValue.item == null) strings.titleNew else strings.titleEdit,
        formData = formData,
        saver = screenModel::save,
        onDismissRequest = { dialogState.value = DialogState.Closed() }
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = strings.labelName,
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH
            )

            TextInput(
                label = strings.labelDescription,
                value = formData.description,
                validate = validate,
                maxLength = Skill.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )

            ChipList(
                label = strings.labelCharacteristic,
                items = Characteristic.values()
                    .map { it to it.getShortcutName() },
                value = formData.characteristic.value,
                onValueChange = { formData.characteristic.value = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                CheckboxWithText(
                    text = strings.labelAdvanced,
                    checked = formData.advanced.value,
                    onCheckedChange = { formData.advanced.value = it }
                )
            }
        }
    }
}
