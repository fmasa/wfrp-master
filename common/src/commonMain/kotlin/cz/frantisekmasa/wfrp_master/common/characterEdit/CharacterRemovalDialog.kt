package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.ConfirmationDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CharacterRemovalDialog(
    character: Character,
    onDismissRequest: () -> Unit,
    onConfirmation: suspend () -> Unit,
) {
    ConfirmationDialog(
        text = LocalStrings.current.character.messages.removalDialogText(character.name),
        confirmationButtonText = LocalStrings.current.commonUi.buttonRemove,
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
    )
}
