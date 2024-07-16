package cz.frantisekmasa.wfrp_master.common.compendium.disease

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DiseaseDialog(
    disease: Disease?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Disease) -> Unit,
) {
    val formData = DiseaseFormData.fromItem(disease)

    CompendiumItemDialog(
        title =
            stringResource(
                if (disease == null) {
                    Str.diseases_title_new
                } else {
                    Str.diseases_title_edit
                },
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
                label = stringResource(Str.diseases_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Disease.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.diseases_label_contraction),
                value = formData.contraction,
                validate = validate,
                maxLength = Disease.CONTRACTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )

            TextInput(
                label = stringResource(Str.diseases_label_incubation),
                value = formData.incubation,
                validate = validate,
                maxLength = Disease.INCUBATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.diseases_label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Disease.DURATION_MAX_LENGTH,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )

            TextInput(
                label = stringResource(Str.diseases_label_symptoms),
                value = formData.symptoms,
                validate = validate,
                maxLength = Disease.SYMPTOMS_MAX_LENGTH,
                helperText = stringResource(Str.diseases_symptoms_helper),
            )
            TextInput(
                label = stringResource(Str.diseases_label_description),
                value = formData.description,
                validate = validate,
                maxLength = Disease.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )
            TextInput(
                label = stringResource(Str.diseases_label_permanent_effects_optional),
                value = formData.permanentEffects,
                validate = validate,
                maxLength = Disease.PERMANENT_EFFECTS_MAX_LENGTH,
            )
        }
    }
}

@Stable
private data class DiseaseFormData(
    val id: Uuid,
    val isVisibleToPlayers: Boolean,
    val name: InputValue,
    val description: InputValue,
    val contraction: InputValue,
    val incubation: InputValue,
    val duration: InputValue,
    val symptoms: InputValue,
    val permanentEffects: InputValue,
) : CompendiumItemFormData<Disease> {
    companion object {
        @Composable
        fun fromItem(item: Disease?) =
            DiseaseFormData(
                id = remember(item) { item?.id ?: uuid4() },
                isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
                name = inputValue(item?.name ?: "", Rules.NotBlank()),
                duration = inputValue(item?.duration ?: "", Rules.NotBlank()),
                description = inputValue(item?.description ?: "", Rules.NotBlank()),
                contraction = inputValue(item?.contraction ?: "", Rules.NotBlank()),
                incubation = inputValue(item?.incubation ?: ""),
                symptoms = inputValue(item?.symptoms?.joinToString(", ") ?: "", Rules.NotBlank()),
                permanentEffects = inputValue(item?.permanentEffects ?: ""),
            )
    }

    override fun toValue() =
        Disease(
            id = id,
            name = name.value,
            isVisibleToPlayers = isVisibleToPlayers,
            description = description.value,
            contraction = contraction.value,
            incubation = incubation.value,
            duration = duration.value,
            symptoms =
                symptoms.value.split(",")
                    .filter { it.isNotBlank() }
                    .map { it.trim() },
            permanentEffects = permanentEffects.value,
        )

    override fun isValid() =
        listOf(
            name,
            duration,
            description,
            contraction,
            incubation,
            symptoms,
            permanentEffects,
        ).all { it.isValid() }
}
