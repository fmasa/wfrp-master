package cz.frantisekmasa.wfrp_master.common.character.traits.dialog

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddTraitDialog(screenModel: TraitsScreenModel, onDismissRequest: () -> Unit) {
    var state: AddTraitDialogState by rememberSaveable { mutableStateOf(ChoosingCompendiumTrait) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != ChoosingCompendiumTrait) {
                state = ChoosingCompendiumTrait
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (val currentState = state) {
            ChoosingCompendiumTrait -> {
                val coroutineScope = rememberCoroutineScope()
                var saving by remember { mutableStateOf(false) }

                if (saving) {
                    Surface {
                        FullScreenProgress()
                    }
                    return@FullScreenDialog
                }

                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = LocalStrings.current.traits.titleChooseCompendiumTrait,
                    onDismissRequest = onDismissRequest,
                    icon = { Resources.Drawable.Trait },
                    onSelect = {
                        if (it.specifications.isEmpty()) {
                            saving = true
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.saveCompendiumTrait(uuid4(), it.id, emptyMap())
                                onDismissRequest()
                            }
                        } else {
                            state = FillingInTimesSpecifications(it)
                        }
                    },
                    emptyUiIcon = Resources.Drawable.Trait,
                )
            }
            is FillingInTimesSpecifications ->
                TraitSpecificationsForm(
                    existingTrait = null,
                    compendiumTraitId = currentState.compendiumTrait.id,
                    screenModel = screenModel,
                    onDismissRequest = onDismissRequest,
                    defaultSpecifications = currentState.compendiumTrait.specifications.associateWith { "" },
                )
        }
    }
}

private sealed class AddTraitDialogState : Parcelable

@Parcelize
private class FillingInTimesSpecifications(val compendiumTrait: Trait) : AddTraitDialogState()

@Parcelize
private object ChoosingCompendiumTrait : AddTraitDialogState()
