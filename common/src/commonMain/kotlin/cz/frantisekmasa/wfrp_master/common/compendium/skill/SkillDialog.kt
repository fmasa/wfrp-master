package cz.frantisekmasa.wfrp_master.common.compendium.skill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.checkboxValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SkillDialog(
    skill: Skill?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Skill) -> Unit,
) {
    val formData = SkillFormData.fromItem(skill)

    CompendiumItemDialog(
        title = stringResource(
            if (skill == null)
                Str.skills_title_new
            else Str.skills_title_edit
        ),
        formData = formData,
        saver = onSaveRequest,
        onDismissRequest = onDismissRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(Str.skills_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(Str.skills_label_description),
                value = formData.description,
                validate = validate,
                maxLength = Skill.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )

            ChipList(
                label = stringResource(Str.skills_label_characteristic),
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
                    text = stringResource(Str.skills_label_advanced),
                    checked = formData.advanced.value,
                    onCheckedChange = { formData.advanced.value = it }
                )
            }
        }
    }
}

@Stable
private data class SkillFormData(
    val id: Uuid,
    val name: InputValue,
    val description: InputValue,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<Skill> {
    companion object {
        @Composable
        fun fromItem(item: Skill?) = SkillFormData(
            id = remember { item?.id ?: uuid4() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            description = inputValue(item?.description ?: ""),
            characteristic = rememberSaveable { mutableStateOf(item?.characteristic ?: Characteristic.AGILITY) },
            advanced = checkboxValue(item?.advanced ?: false),
            isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
        )
    }

    override fun toValue() = Skill(
        id = id,
        name = name.value,
        description = description.value,
        characteristic = characteristic.value,
        advanced = advanced.value,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    override fun isValid() =
        name.isValid() &&
            name.value.length <= Skill.NAME_MAX_LENGTH &&
            description.value.length <= Skill.DESCRIPTION_MAX_LENGTH
}
