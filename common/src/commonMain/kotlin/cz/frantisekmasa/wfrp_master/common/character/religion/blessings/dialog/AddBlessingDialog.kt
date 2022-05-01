package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog


@Composable
internal fun AddBlessingDialog(screenModel: BlessingsScreenModel, onDismissRequest: () -> Unit) {
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
                    screenModel = screenModel,
                    onComplete = onDismissRequest,
                    onCustomBlessingRequest = { state = FillingInCustomMiracle },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomMiracle -> NonCompendiumBlessingForm(
                screenModel = screenModel,
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
