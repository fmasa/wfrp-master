package cz.frantisekmasa.wfrp_master.common.character.trappings.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.trappings.CharacterTrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.NonCompendiumTrappingForm
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingFromCompendiumForm
import cz.frantisekmasa.wfrp_master.common.character.trappings.trappingIcon
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

class AddTrappingScreen(
    private val characterId: CharacterId,
    private val containerId: Uuid?,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTrappingScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        val (step, setStep) = rememberSaveable { mutableStateOf<Step>(Step.ChoosingCompendiumTrapping) }

        if (state == null) {
            FullScreenProgress()
            return
        }

        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val successMessage = stringResource(Str.common_ui_item_added)

        when (step) {
            Step.ChoosingCompendiumTrapping -> {
                val navigation = LocalNavigationTransaction.current

                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.trappings_title_choose_compendium_trapping),
                    onDismissRequest = navigation::goBack,
                    icon = { trappingIcon(it.trappingType) },
                    onSelect = { setStep(Step.FillingInDetails(it)) },
                    onCustomItemRequest = { setStep(Step.FillingInCustomTrapping) },
                    customItemButtonText = stringResource(Str.trappings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.TrappingContainer,
                )
            }

            Step.FillingInCustomTrapping -> NonCompendiumTrappingForm(
                existingItem = null,
                onSaveRequest = {
                    screenModel.saveTrapping(it)
                    snackbarHolder.showSnackbar(successMessage)
                },
                onDismissRequest = {},
                defaultContainerId = null,
            )

            is Step.FillingInDetails -> {
                val navigation = LocalNavigationTransaction.current
                TrappingFromCompendiumForm(
                    itemName = step.compendiumItem.name,
                    itemQualities = emptySet(),
                    itemFlaws = emptySet(),
                    quantity = 1,
                    onSaveRequest = { itemQualities, itemFlaws, quantity ->
                        val item = InventoryItem.fromCompendium(
                            step.compendiumItem,
                            itemQualities,
                            itemFlaws,
                            quantity,
                        )

                        screenModel.saveTrapping(
                            if (containerId != null)
                                item.addToContainer(containerId)
                            else item
                        )

                        snackbarHolder.showSnackbar(successMessage)
                        navigation.replace(CharacterTrappingDetailScreen(characterId, item.id))
                    },
                    onDismissRequest = { setStep(Step.ChoosingCompendiumTrapping) },
                )
            }
        }
    }

    private sealed class Step : Parcelable {
        @Parcelize
        object ChoosingCompendiumTrapping : Step()
        @Parcelize
        object FillingInCustomTrapping : Step()
        @Parcelize
        data class FillingInDetails(val compendiumItem: Trapping) : Step()
    }
}
