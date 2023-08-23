package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddTrappingDialog(
    containerId: Uuid?,
    screenModel: TrappingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    val (state, setState) = rememberSaveable { mutableStateOf<State>(State.ChoosingCompendiumTrapping) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != State.ChoosingCompendiumTrapping) {
                setState(State.ChoosingCompendiumTrapping)
            } else {
                onDismissRequest()
            }
        }
    ) {
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val successMessage = stringResource(Str.common_ui_item_added)

        when (state) {
            State.ChoosingCompendiumTrapping -> {
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = stringResource(Str.trappings_title_choose_compendium_trapping),
                    onDismissRequest = onDismissRequest,
                    icon = { trappingIcon(it.trappingType) },
                    onSelect = { setState(State.FillingInItemQualitiesAndFlaws(it)) },
                    onCustomItemRequest = { setState(State.FillingInCustomTrapping) },
                    customItemButtonText = stringResource(Str.blessings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.TrappingContainer,
                )
            }

            State.FillingInCustomTrapping -> NonCompendiumTrappingForm(
                existingItem = null,
                onSaveRequest = {
                    screenModel.saveItem(it)
                    snackbarHolder.showSnackbar(successMessage)
                },
                onDismissRequest = onDismissRequest,
                defaultContainerId = null,
            )

            is State.FillingInItemQualitiesAndFlaws -> {
                val navigation = LocalNavigationTransaction.current
                ItemQualitiesAndFlawsForm(
                    itemName = state.compendiumItem.name,
                    itemQualities = emptySet(),
                    itemFlaws = emptySet(),
                    onSaveRequest = { itemQualities, itemFlaws ->
                        val item = InventoryItem.fromCompendium(state.compendiumItem, itemQualities, itemFlaws)

                        screenModel.saveItem(
                            if (containerId != null)
                                item.addToContainer(containerId)
                            else item
                        )

                        snackbarHolder.showSnackbar(successMessage)
                        onDismissRequest()
                        navigation.navigate(TrappingDetailScreen(screenModel.characterId, item.id))
                    },
                    onDismissRequest = { setState(State.ChoosingCompendiumTrapping) },
                )
            }
        }
    }
}

private sealed class State : Parcelable {
    @Parcelize
    object ChoosingCompendiumTrapping : State()
    @Parcelize
    object FillingInCustomTrapping : State()
    @Parcelize
    data class FillingInItemQualitiesAndFlaws(val compendiumItem: Trapping) : State()
}
