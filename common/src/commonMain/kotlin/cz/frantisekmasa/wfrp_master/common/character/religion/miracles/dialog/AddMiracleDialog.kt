package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
internal fun AddMiracleDialog(screenModel: MiraclesScreenModel, onDismissRequest: () -> Unit) {
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
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = LocalStrings.current.miracles.titleChooseCompendiumMiracle,
                    onDismissRequest = onDismissRequest,
                    icon = { Resources.Drawable.Miracle },
                    onSelect = { screenModel.saveItem(Miracle.fromCompendium(it)) },
                    onCustomItemRequest = { state = FillingInCustomMiracle },
                    customItemButtonText = LocalStrings.current.miracles.buttonAddNonCompendium,
                    emptyUiIcon = Resources.Drawable.Miracle,
                )
            is FillingInCustomMiracle -> NonCompendiumMiracleForm(
                screenModel = screenModel,
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
