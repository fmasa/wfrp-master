package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun AddTalentDialog(screenModel: TalentsScreenModel, onDismissRequest: () -> Unit) {
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
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = LocalStrings.current.talents.titleChooseCompendiumTalent,
                    onDismissRequest = onDismissRequest,
                    icon = { Resources.Drawable.Talent },
                    onSelect = { state = FillingInTimesTaken(it.id) },
                    onCustomItemRequest = { state = FillingInCustomTalent },
                    customItemButtonText = LocalStrings.current.talents.buttonAddNonCompendium,
                    emptyUiIcon = Resources.Drawable.Talent,
                )
            is FillingInTimesTaken ->
                TimesTakenForm(
                    existingTalent = null,
                    compendiumTalentId = currentState.compendiumTalentId,
                    screenModel = screenModel,
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomTalent -> NonCompendiumTalentForm(
                screenModel = screenModel,
                existingTalent = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private sealed class AddTalentDialogState : Parcelable

@Parcelize
private class FillingInTimesTaken(val compendiumTalentId: Uuid) : AddTalentDialogState()

@Parcelize
private object ChoosingCompendiumTalent : AddTalentDialogState()

@Parcelize
private object FillingInCustomTalent : AddTalentDialogState()
