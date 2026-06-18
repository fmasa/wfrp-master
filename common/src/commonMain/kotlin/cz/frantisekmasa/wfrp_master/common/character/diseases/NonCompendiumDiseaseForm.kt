package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Countdown
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease as CompendiumDisease

@Composable
fun NonCompendiumDiseaseForm(
    onSave: suspend (Disease) -> Unit,
    existingDisease: Disease?,
    onDismissRequest: () -> Unit,
    isGameMaster: Boolean,
) {
    val formData = NonCompendiumDiseaseFormData.fromDisease(existingDisease)

    FormDialog(
        title =
            stringResource(
                if (existingDisease != null) Str.diseases_title_edit else Str.diseases_title_new,
            ),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = onSave,
    ) { validate ->

        if (isGameMaster) {
            SubheadBar {
                DiagnosedSwitch(
                    diagnosed = formData.isDiagnosed.value,
                    onChange = { formData.isDiagnosed.value = it },
                )
            }
        }

        TextInput(
            label = stringResource(Str.diseases_label_name),
            value = formData.name,
            validate = validate,
            maxLength = CompendiumDisease.NAME_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(Str.diseases_label_symptoms),
            value = formData.symptoms,
            validate = validate,
            maxLength = CompendiumDisease.SYMPTOMS_MAX_LENGTH,
            helperText = stringResource(Str.diseases_symptoms_helper),
        )

        TextInput(
            label = stringResource(Str.diseases_label_permanent_effects),
            value = formData.permanentEffects,
            validate = validate,
            maxLength = CompendiumDisease.PERMANENT_EFFECTS_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(Str.diseases_label_description),
            value = formData.description,
            validate = validate,
            maxLength = CompendiumDisease.DESCRIPTION_MAX_LENGTH,
        )

        CountdownInput(
            label = stringResource(Str.diseases_label_incubation),
            data = formData.incubation,
            validate = validate,
        )

        CountdownInput(
            label = stringResource(Str.diseases_label_duration),
            data = formData.duration,
            validate = validate,
        )
    }
}

data class NonCompendiumDiseaseFormData(
    val id: Uuid,
    val name: InputValue,
    val symptoms: InputValue,
    val permanentEffects: InputValue,
    val description: InputValue,
    val incubation: CountdownInputData,
    val duration: CountdownInputData,
    val isDiagnosed: MutableState<Boolean>,
) : HydratedFormData<Disease> {
    override fun toValue(): Disease {
        return Disease(
            id = id,
            name = name.value,
            symptoms =
                symptoms.value.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },
            permanentEffects = permanentEffects.value,
            description = description.value,
            incubation = incubation.toValue(),
            duration = duration.toValue(),
            isDiagnosed = isDiagnosed.value,
        )
    }

    override fun isValid(): Boolean {
        return listOf(
            name,
            symptoms,
            permanentEffects,
            description,
            incubation.value,
            duration.value,
        ).all { it.isValid() }
    }

    companion object {
        @Composable
        fun fromDisease(disease: Disease?): NonCompendiumDiseaseFormData {
            return NonCompendiumDiseaseFormData(
                id = remember { disease?.id ?: uuid4() },
                name = inputValue(disease?.name ?: ""),
                symptoms = inputValue(disease?.symptoms?.joinToString(", ") ?: ""),
                permanentEffects = inputValue(disease?.permanentEffects ?: ""),
                description = inputValue(disease?.description ?: ""),
                incubation =
                    CountdownInputData(
                        value = inputValue(disease?.incubation?.value?.toString() ?: "", Rules.NonNegativeInteger()),
                        unit = rememberSaveable { mutableStateOf(disease?.incubation?.unit ?: Countdown.Unit.DAYS) },
                    ),
                duration =
                    CountdownInputData(
                        value = inputValue(disease?.duration?.value?.toString() ?: "", Rules.NonNegativeInteger()),
                        unit = rememberSaveable { mutableStateOf(disease?.duration?.unit ?: Countdown.Unit.DAYS) },
                    ),
                isDiagnosed = rememberSaveable { mutableStateOf(disease?.isDiagnosed ?: false) },
            )
        }
    }
}
