package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers

@Composable
fun CharacterRemovalDialog(
    character: Character,
    onDismissRequest: () -> Unit,
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

            Text(LocalStrings.current.character.messages.removalDialogText(character.name))
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
                Text(LocalStrings.current.commonUi.buttonRemove)
            }
        },
        dismissButton = {
            TextButton(
                enabled = !processing,
                onClick = onDismissRequest
            ) {
                Text(LocalStrings.current.commonUi.buttonCancel)
            }
        }
    )
}
