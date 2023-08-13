package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    text: AnnotatedString,
    confirmationButtonText: String,
    onConfirmation: suspend () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var processing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            if (processing) {
                DialogProgress()
                return@AlertDialog
            }

            Text(text)
        },
        confirmButton = {
            TextButton(
                enabled = !processing,
                onClick = {
                    processing = true
                    coroutineScope.launchLogged(Dispatchers.IO) {
                        onConfirmation()
                        onDismissRequest()
                    }
                }
            ) {
                Text(confirmationButtonText)
            }
        },
        dismissButton = {
            TextButton(
                enabled = !processing,
                onClick = onDismissRequest
            ) {
                Text(stringResource(Str.common_ui_button_cancel).uppercase())
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    text: String,
    confirmationButtonText: String,
    onConfirmation: suspend () -> Unit,
) {
    ConfirmationDialog(
        onDismissRequest = onDismissRequest,
        text = AnnotatedString(text),
        confirmationButtonText = confirmationButtonText,
        onConfirmation = onConfirmation,
    )
}
