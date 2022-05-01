package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog

@Composable
fun AddSkillDialog(screenModel: SkillsScreenModel, onDismissRequest: () -> Unit) {
    var state: AddSkillDialogState by rememberSaveable { mutableStateOf(ChoosingCompendiumSkill) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != ChoosingCompendiumSkill) {
                state = ChoosingCompendiumSkill
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (val currentState = state) {
            ChoosingCompendiumSkill ->
                CompendiumSkillChooser(
                    screenModel = screenModel,
                    onSkillSelected = { state = FillingInAdvances(it.id, it.advanced) },
                    onCustomSkillRequest = { state = FillingInCustomSkill },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInAdvances ->
                AdvancesForm(
                    compendiumSkillId = currentState.compendiumSkillId,
                    screenModel = screenModel,
                    isAdvanced = currentState.isAdvanced,
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomSkill -> NonCompendiumSkillForm(
                screenModel = screenModel,
                existingSkill = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private sealed class AddSkillDialogState : Parcelable

@Parcelize
private class FillingInAdvances(
    val compendiumSkillId: Uuid,
    val isAdvanced: Boolean,
) : AddSkillDialogState()

@Parcelize
private object ChoosingCompendiumSkill : AddSkillDialogState()

@Parcelize
private object FillingInCustomSkill : AddSkillDialogState()
