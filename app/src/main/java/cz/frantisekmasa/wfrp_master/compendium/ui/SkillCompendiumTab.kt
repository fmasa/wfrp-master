package cz.frantisekmasa.wfrp_master.compendium.ui

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
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
import cz.muni.fi.rpg.R
import java.util.UUID

@Composable
fun SkillCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    CompendiumTab(
        liveItems = viewModel.skills,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_skills_in_compendium,
                subTextId = R.string.no_skills_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_skills
            )
        },
        remover = viewModel::remove,
        saver = viewModel::save,
        dialog = { SkillDialog(it, viewModel) },
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

private data class SkillFormData(
    val id: UUID,
    val name: InputValue,
    val description: InputValue,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
) : CompendiumItemFormData<Skill> {
    companion object {
        @Composable
        fun fromItem(item: Skill?) = SkillFormData(
            id = remember { item?.id ?: UUID.randomUUID() },
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
    viewModel: CompendiumViewModel,
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val formData = SkillFormData.fromItem(dialogStateValue.item)

    CompendiumItemDialog(
        title = stringResource(
            if (dialogStateValue.item == null)
                R.string.title_skill_new
            else R.string.title_skill_edit
        ),
        formData = formData,
        saver = viewModel::save,
        onDismissRequest = { dialogState.value = DialogState.Closed() }
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                maxLength = Skill.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )

            ChipList(
                label = stringResource(R.string.label_skill_characteristic),
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
                    text = stringResource(R.string.label_skill_advanced),
                    checked = formData.advanced.value,
                    onCheckedChange = { formData.advanced.value = it }
                )
            }
        }
    }
}
