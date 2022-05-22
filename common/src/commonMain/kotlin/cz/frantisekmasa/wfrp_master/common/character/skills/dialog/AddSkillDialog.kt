package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = LocalStrings.current.skills.titleChooseCompendiumSkill,
                    onDismissRequest = onDismissRequest,
                    icon = { it.characteristic.getIcon() },
                    onSelect = { state = FillingInAdvances(it.id, it.advanced) },
                    onCustomItemRequest = { state = FillingInCustomSkill },
                    customItemButtonText = LocalStrings.current.skills.buttonAddNonCompendium,
                    emptyUiIcon = Resources.Drawable.Skill,
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
