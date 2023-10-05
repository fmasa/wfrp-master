package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun TimesTakenForm(
    existingTalent: Talent?,
    onSave: suspend (timesTaken: Int) -> TimesTakenForm.SavingResult,
    onDismissRequest: () -> Unit,
) {
    val formData = TimesTakenForm.FormData(
        rememberSaveable { mutableStateOf(existingTalent?.taken ?: 1) }
    )

    val snackbarHolder = LocalPersistentSnackbarHolder.current
    val successMessage = stringResource(
        Str.talents_messages_compendium_talent_removed
    )

    FormDialog(
        title = stringResource(
            if (existingTalent != null)
                Str.talents_title_edit
            else Str.talents_title_new
        ),
        formData = formData,
        onDismissRequest = onDismissRequest,
        onSave = {
            when (onSave(it)) {
                TimesTakenForm.SavingResult.SUCCESS -> {}
                TimesTakenForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED -> {
                    snackbarHolder.showSnackbar(successMessage)
                }
            }

            onDismissRequest()
        }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Str.talents_label_times_taken),
                modifier = Modifier.weight(1f),
            )
            NumberPicker(
                value = formData.timesTaken,
                onIncrement = { formData.timesTaken++ },
                onDecrement = {
                    if (formData.timesTaken > 1) {
                        formData.timesTaken--
                    }
                }
            )
        }
    }
}

object TimesTakenForm {
    enum class SavingResult { SUCCESS, COMPENDIUM_ITEM_WAS_REMOVED }

    @Stable
    data class FormData(
        private val timesTakenState: MutableState<Int>,
    ) : HydratedFormData<Int> {

        var timesTaken by timesTakenState

        override fun isValid(): Boolean = true

        override fun toValue(): Int = timesTaken
    }
}
