package cz.muni.fi.rpg.ui.character.talents.dialog

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.parcelize.Parcelize
import java.util.*

@Composable
fun AddTalentDialog(viewModel: TalentsViewModel, onDismissRequest: () -> Unit) {
    var state: AddTalentDialogState by savedInstanceState { ChoosingCompendiumTalent }

    FullScreenDialog(onDismissRequest = {
        if (state != ChoosingCompendiumTalent) {
            state = ChoosingCompendiumTalent
        } else {
            onDismissRequest()
        }
    }) {
        when (val currentState = state) {
            ChoosingCompendiumTalent ->
                CompendiumTalentChooser(
                    viewModel = viewModel,
                    onTalentSelected = { state = FillingInTimesTaken(it.id) },
                    onCustomTalentRequest = { state = FillingInCustomTalent }
                )
            is FillingInTimesTaken ->
                TimesTakenForm(
                    existingTalent = null,
                    compendiumTalentId = currentState.compendiumTalentId,
                    viewModel = viewModel,
                    onComplete = onDismissRequest,
                )
            is FillingInCustomTalent -> NonCompendiumTalentForm(
                viewModel = viewModel,
                existingTalent = null,
                onComplete = onDismissRequest,
            )
        }
    }
}

private sealed class AddTalentDialogState : Parcelable

@Parcelize
private class FillingInTimesTaken(val compendiumTalentId: UUID) : AddTalentDialogState()

@Parcelize
private object ChoosingCompendiumTalent : AddTalentDialogState()

@Parcelize
private object FillingInCustomTalent : AddTalentDialogState()