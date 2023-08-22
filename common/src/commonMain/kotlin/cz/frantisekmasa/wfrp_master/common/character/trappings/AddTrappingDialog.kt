package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddTrappingDialog(
    containerId: Uuid?,
    screenModel: TrappingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    var state by rememberSaveable { mutableStateOf(State.CHOOSING_COMPENDIUM_TRAPPING) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != State.CHOOSING_COMPENDIUM_TRAPPING) {
                state = State.CHOOSING_COMPENDIUM_TRAPPING
            } else {
                onDismissRequest()
            }
        }
    ) {
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val successMessage = stringResource(Str.common_ui_item_added)

        when (state) {
            State.CHOOSING_COMPENDIUM_TRAPPING -> {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = stringResource(Str.trappings_title_choose_compendium_trapping),
                    onDismissRequest = onDismissRequest,
                    icon = { trappingIcon(it.trappingType) },
                    onSelect = {
                        val item = InventoryItem.fromCompendium(it)
                        screenModel.saveItem(
                            if (containerId != null)
                                item.addToContainer(containerId)
                            else item
                        )

                        snackbarHolder.showSnackbar(successMessage)
                        onDismissRequest()
                        navigation.navigate(TrappingDetailScreen(screenModel.characterId, item.id))
                    },
                    onCustomItemRequest = { state = State.FILLING_IN_CUSTOM_TRAPPING },
                    customItemButtonText = stringResource(Str.blessings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.TrappingContainer,
                )
            }

            State.FILLING_IN_CUSTOM_TRAPPING -> NonCompendiumTrappingForm(
                existingItem = null,
                onSaveRequest = {
                    screenModel.saveItem(it)
                    snackbarHolder.showSnackbar(successMessage)
                },
                onDismissRequest = onDismissRequest,
                defaultContainerId = null,
            )
        }
    }
}

private enum class State {
    CHOOSING_COMPENDIUM_TRAPPING,
    FILLING_IN_CUSTOM_TRAPPING,
}
