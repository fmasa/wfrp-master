package cz.muni.fi.rpg.ui.character.spells.dialog

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.parcelize.Parcelize

@Composable
fun AddSpellDialog(viewModel: SpellsViewModel, onDismissRequest: () -> Unit) {
    var state: AddSpellDialogState by rememberSaveable { mutableStateOf(ChoosingCompendiumSpell) }

    FullScreenDialog(onDismissRequest = {
        if (state != ChoosingCompendiumSpell) {
            state = ChoosingCompendiumSpell
        } else {
            onDismissRequest()
        }
    }) {
        when (state) {
            ChoosingCompendiumSpell ->
                CompendiumSpellChooser(
                    viewModel = viewModel,
                    onComplete = onDismissRequest,
                    onCustomSpellRequest = { state = FillingInCustomSpell },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomSpell -> NonCompendiumSpellForm(
                viewModel = viewModel,
                existingSpell = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private sealed class AddSpellDialogState : Parcelable

@Parcelize
private object ChoosingCompendiumSpell : AddSpellDialogState()

@Parcelize
private object FillingInCustomSpell : AddSpellDialogState()