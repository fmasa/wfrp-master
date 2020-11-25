package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.CompendiumItem
import cz.muni.fi.rpg.ui.common.composables.CloseButton
import cz.muni.fi.rpg.ui.common.composables.SaveAction
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
import cz.muni.fi.rpg.ui.common.composables.dialog.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private enum class FormState {
    EDITED_BY_USER,
    SAVING,
}

@Composable
internal fun <T : CompendiumItem> CompendiumItemDialog(
    title: String,
    formData: CompendiumItemFormData<T>,
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
                Progress()
                return@Scaffold
            }
            ScrollableColumn {
                form(validate.value)
            }
        }
    }
}

@Composable
private fun <T : CompendiumItem> CompendiumItemDialogTopBar(
    title: String,
    formData: CompendiumItemFormData<T>,
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
                    Timber.d("Save action clicked")
                    if (formData.isValid()) {
                        Timber.d("Saving item")

                        formState.value = FormState.SAVING
                        coroutineScope.launch(Dispatchers.IO) {
                            saver(formData.toItem())
                            withContext(Dispatchers.Main) {
                                onDismissRequest()
                            }
                        }
                    } else {
                        Timber.d("Form values are invalid")
                        validate.value = true
                    }
                }
            )
        }
    )
}

