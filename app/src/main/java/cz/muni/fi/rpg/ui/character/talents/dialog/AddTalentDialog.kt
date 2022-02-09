package cz.muni.fi.rpg.ui.character.talents.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Composable
fun AddTalentDialog(viewModel: TalentsViewModel, onDismissRequest: () -> Unit) {
    var state: AddTalentDialogState by rememberSaveable { mutableStateOf(ChoosingCompendiumTalent) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != ChoosingCompendiumTalent) {
                state = ChoosingCompendiumTalent
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (val currentState = state) {
            ChoosingCompendiumTalent ->
                CompendiumTalentChooser(
                    viewModel = viewModel,
                    onTalentSelected = { state = FillingInTimesTaken(it.id) },
                    onCustomTalentRequest = { state = FillingInCustomTalent },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInTimesTaken ->
                TimesTakenForm(
                    existingTalent = null,
                    compendiumTalentId = currentState.compendiumTalentId,
                    viewModel = viewModel,
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomTalent -> NonCompendiumTalentForm(
                viewModel = viewModel,
                existingTalent = null,
                onDismissRequest = onDismissRequest,
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
