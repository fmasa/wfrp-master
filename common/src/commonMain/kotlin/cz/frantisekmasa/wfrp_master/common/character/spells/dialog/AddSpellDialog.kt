package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog

@Composable
fun AddSpellDialog(screenModel: SpellsScreenModel, onDismissRequest: () -> Unit) {
    var state: AddSpellDialogState by rememberSaveable { mutableStateOf(AddSpellDialogState.ChoosingCompendiumSpell) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != AddSpellDialogState.ChoosingCompendiumSpell) {
                state = AddSpellDialogState.ChoosingCompendiumSpell
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (state) {
            AddSpellDialogState.ChoosingCompendiumSpell ->
                CompendiumSpellChooser(
                    screenModel = screenModel,
                    onComplete = onDismissRequest,
                    onCustomSpellRequest = { state = AddSpellDialogState.FillingInCustomSpell },
                    onDismissRequest = onDismissRequest,
                )
            AddSpellDialogState.FillingInCustomSpell -> NonCompendiumSpellForm(
                screenModel = screenModel,
                existingSpell = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private enum class AddSpellDialogState {
    ChoosingCompendiumSpell,
    FillingInCustomSpell,
}


