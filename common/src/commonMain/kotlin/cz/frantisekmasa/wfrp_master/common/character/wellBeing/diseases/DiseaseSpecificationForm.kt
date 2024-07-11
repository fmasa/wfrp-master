package cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.diseases.CountdownInput
import cz.frantisekmasa.wfrp_master.common.character.diseases.CountdownInputData
import cz.frantisekmasa.wfrp_master.common.character.diseases.DiagnosedSwitch
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Countdown
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease as CompendiumDisease

@Composable
fun DiseaseSpecificationForm(
    isGameMaster: Boolean,
    compendiumDisease: CompendiumDisease?,
    existingDisease: DiseaseSpecification.Data?,
    onSave: suspend (DiseaseSpecification.Data) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val formData = DiseaseSpecification.FormData.fromDisease(existingDisease, isGameMaster)

    FormDialog(
        title =
            stringResource(
                if (existingDisease != null) {
                    Str.diseases_title_edit
                } else {
                    Str.diseases_title_new
                },
            ),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = onSave,
    ) { validate ->
        if (isGameMaster) {
            DiagnosedSwitch(
                diagnosed = formData.isDiagnosed.value,
                onChange = { formData.isDiagnosed.value = it },
            )
        }

        CountdownInput(
            label = stringResource(Str.diseases_label_incubation),
            data = formData.incubation,
            helperText = compendiumDisease?.incubation,
            validate = validate,
        )

        CountdownInput(
            label = stringResource(Str.diseases_label_duration),
            data = formData.duration,
            helperText = compendiumDisease?.duration,
            validate = validate,
        )
    }
}

object DiseaseSpecification {
    data class Data(
        val isDiagnosed: Boolean,
        val incubation: Countdown,
        val duration: Countdown,
    )

    data class FormData(
        val isDiagnosed: MutableState<Boolean>,
        val incubation: CountdownInputData,
        val duration: CountdownInputData,
    ) : HydratedFormData<Data> {
        override fun isValid() = incubation.isValid() && duration.isValid()

        override fun toValue() =
            Data(
                isDiagnosed = isDiagnosed.value,
                incubation = incubation.toValue(),
                duration = duration.toValue(),
            )

        companion object {
            @Composable
            fun fromDisease(
                disease: Data?,
                isGameMaster: Boolean,
            ): FormData {
                return FormData(
                    isDiagnosed = rememberSaveable { mutableStateOf(disease?.isDiagnosed ?: !isGameMaster) },
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
                )
            }
        }
    }
}
