package cz.frantisekmasa.wfrp_master.common.compendium.talent

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
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TalentDialog(
    talent: Talent?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Talent) -> Unit,
) {
    val formData = TalentFormData.fromTalent(talent)

    CompendiumItemDialog(
        onDismissRequest = onDismissRequest,
        title =
            stringResource(
                if (talent == null) {
                    Str.talents_title_new
                } else {
                    Str.talents_title_edit
                },
            ),
        formData = formData,
        saver = onSaveRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(Str.talents_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Talent.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.talents_label_tests),
                value = formData.tests,
                validate = validate,
                maxLength = Talent.TESTS_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.talents_label_max_times_taken),
                value = formData.maxTimesTaken,
                validate = validate,
                maxLength = Talent.MAX_TIMES_TAKEN_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.talents_label_description),
                value = formData.description,
                validate = validate,
                maxLength = Talent.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )
        }
    }
}

@Stable
private data class TalentFormData(
    val id: Uuid,
    val name: InputValue,
    val tests: InputValue,
    val maxTimesTaken: InputValue,
    val description: InputValue,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<Talent> {
    companion object {
        @Composable
        fun fromTalent(talent: Talent?) =
            TalentFormData(
                id = remember { talent?.id ?: uuid4() },
                name = inputValue(talent?.name ?: "", Rules.NotBlank()),
                tests = inputValue(talent?.tests ?: ""),
                maxTimesTaken = inputValue(talent?.maxTimesTaken ?: ""),
                description = inputValue(talent?.description ?: ""),
                isVisibleToPlayers = talent?.isVisibleToPlayers ?: false,
            )
    }

    override fun toValue() =
        Talent(
            id = id,
            name = name.value,
            tests = tests.value.trim(),
            maxTimesTaken = maxTimesTaken.value,
            description = description.value,
            isVisibleToPlayers = isVisibleToPlayers,
        )

    override fun isValid() = listOf(name, maxTimesTaken, description).all { it.isValid() }
}
