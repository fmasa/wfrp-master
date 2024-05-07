package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EncounterDialog(
    existingEncounter: Encounter?,
    onDismissRequest: () -> Unit,
    screenModel: EncountersScreenModel,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formData = EncounterDialogFormData.fromEncounter(existingEncounter)

        FormDialog(
            title =
                stringResource(
                    if (existingEncounter != null) {
                        Str.encounters_title_edit
                    } else {
                        Str.encounters_title_create
                    },
                ),
            onDismissRequest = onDismissRequest,
            formData = formData,
            onSave = {
                if (existingEncounter != null) {
                    screenModel.updateEncounter(existingEncounter.id, it.name, it.description)
                } else {
                    screenModel.createEncounter(it.name, it.description)
                }
            },
        ) { validate ->
            TextInput(
                label = stringResource(Str.encounters_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Encounter.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.encounters_label_description),
                value = formData.description,
                validate = validate,
                maxLength = Encounter.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}

@Stable
private class EncounterDialogFormData(
    val name: InputValue,
    val description: InputValue,
) : HydratedFormData<EncounterDialogFormData.Value> {
    data class Value(
        val name: String,
        val description: String,
    )

    override fun isValid(): Boolean = name.isValid() && description.isValid()

    override fun toValue(): Value = Value(name = name.value, description = description.value)

    companion object {
        @Composable
        fun fromEncounter(encounter: Encounter?) =
            EncounterDialogFormData(
                name = inputValue(encounter?.name ?: "", Rules.NotBlank()),
                description = inputValue(encounter?.description ?: ""),
            )
    }
}
