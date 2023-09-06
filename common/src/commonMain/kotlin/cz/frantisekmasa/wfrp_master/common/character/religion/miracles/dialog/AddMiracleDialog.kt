package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

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
                    title = stringResource(Str.miracles_title_choose_compendium_miracle),
                    onDismissRequest = onDismissRequest,
                    icon = { Resources.Drawable.Miracle },
                    onSelect = { screenModel.saveItem(Miracle.fromCompendium(it)) },
                    onCustomItemRequest = { state = FillingInCustomMiracle },
                    customItemButtonText = stringResource(Str.miracles_button_add_non_compendium),
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
