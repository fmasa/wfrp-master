package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.foundation.layout.Arrangement
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
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.FormInputHorizontalPadding
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import java.util.UUID

@Composable
internal fun NonCompendiumTalentForm(
    viewModel: TalentsViewModel,
    existingTalent: Talent?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumTalentFormData.fromTalent(existingTalent)
    val strings = LocalStrings.current.talents

    FormDialog(
        title = if (existingTalent != null) strings.titleEdit else strings.titleNew,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveTalent,
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
                maxLength = Talent.NAME_MAX_LENGTH,
            )

            NumberPicker(
                label = strings.labelTimesTaken,
                value = formData.taken.value,
                onIncrement = { formData.taken.value++ },
                onDecrement = {
                    formData.taken.value = (formData.taken.value - 1).coerceAtLeast(1)
                }
            )
        }

        TextInput(
            label = strings.labelDescription,
            value = formData.description,
            validate = validate,
            multiLine = true,
            maxLength = Talent.DESCRIPTION_MAX_LENGTH,
        )
    }
}

@Stable
private class NonCompendiumTalentFormData(
    val id: UUID,
    val name: InputValue,
    val description: InputValue,
    val taken: MutableState<Int>,
) : HydratedFormData<Talent> {
    companion object {
        @Composable
        fun fromTalent(talent: Talent?): NonCompendiumTalentFormData = NonCompendiumTalentFormData(
            id = remember { talent?.id ?: UUID.randomUUID() },
            name = inputValue(talent?.name ?: "", Rules.NotBlank()),
            description = inputValue(talent?.description ?: ""),
            taken = rememberSaveable { mutableStateOf(talent?.taken ?: 1) },
        )
    }

    override fun toValue(): Talent = Talent(
        id = id,
        compendiumId = null,
        name = name.value,
        description = description.value,
        taken = taken.value,
    )

    override fun isValid() = name.isValid() && description.isValid() && taken.value > 0
}
