package cz.muni.fi.rpg.ui.character.spells.dialog

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.android.parcel.Parcelize

@Composable
fun AddSpellDialog(viewModel: SpellsViewModel, onDismissRequest: () -> Unit) {
    var state: AddSpellDialogState by savedInstanceState { ChoosingCompendiumSpell }

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
                )
            is FillingInCustomSpell -> NonCompendiumSpellForm(
                viewModel = viewModel,
                existingSpell = null,
                onComplete = onDismissRequest,
            )
        }
    }
}

private sealed class AddSpellDialogState : Parcelable

@Parcelize
private object ChoosingCompendiumSpell : AddSpellDialogState()

@Parcelize
private object FillingInCustomSpell : AddSpellDialogState()