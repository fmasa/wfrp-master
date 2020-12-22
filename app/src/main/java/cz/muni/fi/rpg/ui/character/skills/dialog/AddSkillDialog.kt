package cz.muni.fi.rpg.ui.character.skills.dialog

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.parcelize.Parcelize
import java.util.*

@Composable
fun AddSkillDialog(viewModel: SkillsViewModel, onDismissRequest: () -> Unit) {
    var state: AddSkillDialogState by savedInstanceState { ChoosingCompendiumSkill }

    FullScreenDialog(onDismissRequest = {
        if (state != ChoosingCompendiumSkill) {
            state = ChoosingCompendiumSkill
        } else {
            onDismissRequest()
        }
    }) {
        when (val currentState = state) {
            ChoosingCompendiumSkill ->
                CompendiumSkillChooser(
                    viewModel = viewModel,
                    onSkillSelected = { state = FillingInAdvances(it.id) },
                    onCustomSkillRequest = { state = FillingInCustomSkill }
                )
            is FillingInAdvances ->
                AdvancesForm(
                    existingSkill = null,
                    compendiumSkillId = currentState.compendiumSkillId,
                    viewModel = viewModel,
                    onComplete = onDismissRequest,
                )
            is FillingInCustomSkill -> NonCompendiumSkillForm(
                viewModel = viewModel,
                existingSkill = null,
                onComplete = onDismissRequest,
            )
        }
    }
}

private sealed class AddSkillDialogState : Parcelable

@Parcelize
private class FillingInAdvances(val compendiumSkillId: UUID) : AddSkillDialogState()

@Parcelize
private object ChoosingCompendiumSkill : AddSkillDialogState()

@Parcelize
private object FillingInCustomSkill : AddSkillDialogState()