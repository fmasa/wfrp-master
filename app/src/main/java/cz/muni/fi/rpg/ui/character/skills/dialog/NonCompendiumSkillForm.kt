package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.checkboxValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.common.composables.FormInputHorizontalPadding
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import java.util.UUID

@Composable
internal fun NonCompendiumSkillForm(
    viewModel: SkillsViewModel,
    existingSkill: Skill?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumSkillFormData.fromSkill(existingSkill)
    val strings = LocalStrings.current.skills

    FormDialog(
        title = if (existingSkill != null) strings.titleEdit else strings.titleNew,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveSkill,
    ) { validate ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FormInputHorizontalPadding),
            verticalAlignment = Alignment.Bottom,
        ) {
            TextInput(
                modifier = Modifier.weight(1f),
                label = strings.labelName,
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH,
            )

            NumberPicker(
                label = strings.labelAdvances,
                value = formData.advances.value,
                onIncrement = { formData.advances.value++ },
                onDecrement = {
                    formData.advances.value = (formData.advances.value - 1)
                        .coerceAtLeast(Skill.MIN_ADVANCES)
                }
            )
        }

        TextInput(
            label = strings.labelDescription,
            value = formData.description,
            validate = validate,
            multiLine = true,
            maxLength = Skill.DESCRIPTION_MAX_LENGTH,
        )

        ChipList(
            label = strings.labelCharacteristic,
            items = Characteristic.values().map { it to it.getShortcutName() },
            value = formData.characteristic.value,
            onValueChange = { formData.characteristic.value = it }
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            CheckboxWithText(
                text = strings.labelAdvanced,
                checked = formData.advanced.value,
                onCheckedChange = { formData.advanced.value = it },
            )
        }
    }
}

@Stable
private class NonCompendiumSkillFormData(
    val id: UUID,
    val name: InputValue,
    val description: InputValue,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
    val advances: MutableState<Int>,
) : HydratedFormData<Skill> {
    companion object {
        @Composable
        fun fromSkill(skill: Skill?): NonCompendiumSkillFormData = NonCompendiumSkillFormData(
            id = remember { skill?.id ?: UUID.randomUUID() },
            name = inputValue(skill?.name ?: "", Rules.NotBlank()),
            description = inputValue(skill?.description ?: ""),
            characteristic = rememberSaveable {
                mutableStateOf(
                    skill?.characteristic ?: Characteristic.values().first()
                )
            },
            advanced = checkboxValue(skill?.advanced ?: false),
            advances = rememberSaveable { mutableStateOf(skill?.advances ?: 1) }
        )
    }

    override fun toValue(): Skill = Skill(
        id = id,
        compendiumId = null,
        advanced = advanced.value,
        characteristic = characteristic.value,
        name = name.value,
        description = description.value,
        advances = advances.value
    )

    override fun isValid() = name.isValid() && description.isValid()
}
