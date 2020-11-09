package cz.muni.fi.rpg.ui.character.skills.dialog

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.android.parcel.Parcelize
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun AddSkillDialog(characterId: CharacterId, onDismissRequest: () -> Unit) {
    var state: AddSkillDialogState by savedInstanceState { ChoosingCompendiumSkill }

    FullScreenDialog(onDismissRequest = {
        if (state != ChoosingCompendiumSkill) {
            state = ChoosingCompendiumSkill
        } else {
            onDismissRequest()
        }
    }) {
        val viewModel: SkillsViewModel by viewModel { parametersOf(characterId) }

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