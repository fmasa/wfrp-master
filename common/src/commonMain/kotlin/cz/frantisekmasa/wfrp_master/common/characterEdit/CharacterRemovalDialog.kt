package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.ConfirmationDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CharacterRemovalDialog(
    character: Character,
    onDismissRequest: () -> Unit,
    onConfirmation: suspend () -> Unit,
) {
    ConfirmationDialog(
        text = stringResource(Str.character_messages_removal_dialog_text, character.name),
        confirmationButtonText = stringResource(Str.common_ui_button_remove),
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
    )
}
