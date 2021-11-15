package cz.muni.fi.rpg.ui.character.skills.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Composable
fun AddSkillDialog(viewModel: SkillsViewModel, onDismissRequest: () -> Unit) {
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
                    viewModel = viewModel,
                    onSkillSelected = { state = FillingInAdvances(it.id, it.advanced) },
                    onCustomSkillRequest = { state = FillingInCustomSkill },
                    onDismissRequest = onDismissRequest,
                )
            is FillingInAdvances ->
                AdvancesForm(
                    compendiumSkillId = currentState.compendiumSkillId,
                    viewModel = viewModel,
                    isAdvanced = currentState.isAdvanced,
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomSkill -> NonCompendiumSkillForm(
                viewModel = viewModel,
                existingSkill = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private sealed class AddSkillDialogState : Parcelable

@Parcelize
private class FillingInAdvances(
    val compendiumSkillId: UUID,
    val isAdvanced: Boolean,
) : AddSkillDialogState()

@Parcelize
private object ChoosingCompendiumSkill : AddSkillDialogState()

@Parcelize
private object FillingInCustomSkill : AddSkillDialogState()
