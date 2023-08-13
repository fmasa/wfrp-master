package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddSkillDialog(
    screenModel: SkillsScreenModel,
    characteristics: Stats,
    onDismissRequest: () -> Unit,
) {
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
                    title = stringResource(Str.skills_title_choose_compendium_skill),
                    onDismissRequest = onDismissRequest,
                    icon = { it.characteristic.getIcon() },
                    onSelect = { state = FillingInAdvances(it.id, it.advanced) },
                    onCustomItemRequest = { state = FillingInCustomSkill },
                    customItemButtonText = stringResource(Str.skills_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Skill,
                )
            is FillingInAdvances ->
                AdvancesForm(
                    compendiumSkillId = currentState.compendiumSkillId,
                    screenModel = screenModel,
                    characteristics = characteristics,
                    isAdvanced = currentState.isAdvanced,
                    onDismissRequest = onDismissRequest,
                )
            is FillingInCustomSkill -> NonCompendiumSkillForm(
                screenModel = screenModel,
                existingSkill = null,
                characteristics = characteristics,
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
