package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private enum class FormState {
    EDITED_BY_USER,
    SAVING,
}

@Composable
internal fun <T : CompendiumItem<T>> CompendiumItemDialog(
    title: String,
    formData: HydratedFormData<T>,
    saver: suspend (T) -> Unit,
    onDismissRequest: () -> Unit,
    form: @Composable (validate: Boolean) -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val formState = remember { mutableStateOf(FormState.EDITED_BY_USER) }

        val validate = remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CompendiumItemDialogTopBar(
                    title = title,
                    formData = formData,
                    saver = saver,
                    formState = formState,
                    validate = validate,
                    onDismissRequest = onDismissRequest,
                )
            },
        ) {
            if (formState.value == FormState.SAVING) {
                DialogProgress()
                return@Scaffold
            }
            Column(Modifier.verticalScroll(rememberScrollState())) {
                form(validate.value)
            }
        }
    }
}

@Composable
private fun <T : CompendiumItem<T>> CompendiumItemDialogTopBar(
    title: String,
    formData: HydratedFormData<T>,
    saver: suspend (T) -> Unit,
    formState: MutableState<FormState>,
    validate: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            CloseButton(onClick = onDismissRequest)
        },
        actions = {
            val coroutineScope = rememberCoroutineScope()

            SaveAction(
                enabled = formState.value == FormState.EDITED_BY_USER,
                onClick = {
                    Napier.d("Save action clicked")
                    if (formData.isValid()) {
                        Napier.d("Saving item")

                        formState.value = FormState.SAVING
                        coroutineScope.launch(Dispatchers.IO) {
                            saver(formData.toValue())
                            onDismissRequest()
                        }
                    } else {
                        Napier.d("Form values are invalid")
                        validate.value = true
                    }
                }
            )
        }
    )
}
