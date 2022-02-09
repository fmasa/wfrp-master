package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import kotlinx.parcelize.Parcelize

@Composable
internal fun AddBlessingDialog(viewModel: BlessingsViewModel, onDismissRequest: () -> Unit) {
    var state: AddMiracleDialogState by rememberSaveable { mutableStateOf(ChoosingCompendiumMiracle) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != ChoosingCompendiumMiracle) {
                state = ChoosingCompendiumMiracle
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (state) {
            ChoosingCompendiumMiracle ->
                CompendiumBlessingChooser(
                    viewModel = viewModel,
                    onComplete = onDismissRequest,
                    onCustomBlessingRequest = { state = FillingInCustomMiracle },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomMiracle -> NonCompendiumBlessingForm(
                viewModel = viewModel,
                existingBlessing = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private sealed class AddMiracleDialogState : Parcelable

@Parcelize
private object ChoosingCompendiumMiracle : AddMiracleDialogState()

@Parcelize
private object FillingInCustomMiracle : AddMiracleDialogState()
