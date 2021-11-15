package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EncounterDialog(
    existingEncounter: Encounter?,
    partyId: PartyId,
    onDismissRequest: () -> Unit,
) {
    val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formData = EncounterDialogFormData.fromEncounter(existingEncounter)

        FormDialog(
            title = if (existingEncounter != null)
                R.string.title_encounter_edit
            else R.string.title_encounter_create,
            onDismissRequest = onDismissRequest,
            formData = formData,
            onSave = {
                if (existingEncounter != null) {
                    viewModel.updateEncounter(existingEncounter.id, it.name, it.description)
                } else {
                    viewModel.createEncounter(it.name, it.description)
                }
            }
        ) { validate ->
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = Encounter.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                maxLength = Encounter.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}

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
        fun fromEncounter(encounter: Encounter?) = EncounterDialogFormData(
            name = inputValue(encounter?.name ?: "", Rules.NotBlank()),
            description = inputValue(encounter?.description ?: ""),
        )
    }
}