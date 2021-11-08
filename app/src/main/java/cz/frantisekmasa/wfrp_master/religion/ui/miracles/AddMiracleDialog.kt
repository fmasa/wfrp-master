package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import kotlinx.parcelize.Parcelize

@Composable
internal fun AddMiracleDialog(viewModel: MiraclesViewModel, onDismissRequest: () -> Unit) {
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
                CompendiumMiracleChooser(
                    viewModel = viewModel,
                    onComplete = onDismissRequest,
                    onCustomMiracleRequest = { state = FillingInCustomMiracle },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomMiracle -> NonCompendiumMiracleForm(
                viewModel = viewModel,
                existingMiracle = null,
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
