package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillRating
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
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
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun NonCompendiumSkillForm(
    screenModel: SkillsScreenModel,
    existingSkill: Skill?,
    characteristics: Stats,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumSkillFormData.fromSkill(existingSkill)

    FormDialog(
        title = stringResource(
            if (existingSkill != null) Str.skills_title_edit else Str.skills_title_new,
        ),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = screenModel::saveSkill,
    ) { validate ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.large),
            verticalAlignment = Alignment.Bottom,
        ) {
            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(Str.skills_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH,
            )

            NumberPicker(
                label = stringResource(Str.skills_label_advances),
                value = formData.advances.value,
                onIncrement = { formData.advances.value++ },
                onDecrement = {
                    formData.advances.value = (formData.advances.value - 1)
                        .coerceAtLeast(Skill.MIN_ADVANCES)
                }
            )
        }

        TextInput(
            label = stringResource(Str.skills_label_description),
            value = formData.description,
            validate = validate,
            multiLine = true,
            maxLength = Skill.DESCRIPTION_MAX_LENGTH,
        )

        ChipList(
            label = stringResource(Str.skills_label_characteristic),
            items = Characteristic.values().map { it to stringResource(it.shortcut) },
            value = formData.characteristic.value,
            onValueChange = { formData.characteristic.value = it }
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            CheckboxWithText(
                text = stringResource(Str.skills_label_advanced),
                checked = formData.advanced.value,
                onCheckedChange = { formData.advanced.value = it },
            )
        }

        SkillRating(
            label = stringResource(Str.skills_label_rating),
            value = characteristics.get(formData.characteristic.value) + formData.advances.value,
            modifier = Modifier
                .padding(top = Spacing.extraLarge)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@Stable
private class NonCompendiumSkillFormData(
    val id: Uuid,
    val name: InputValue,
    val description: InputValue,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
    val advances: MutableState<Int>,
) : HydratedFormData<Skill> {
    companion object {
        @Composable
        fun fromSkill(skill: Skill?): NonCompendiumSkillFormData = NonCompendiumSkillFormData(
            id = remember { skill?.id ?: uuid4() },
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
