package cz.frantisekmasa.wfrp_master.common.character.trappings.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.trappings.CharacterTrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.NonCompendiumTrappingForm
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingFromCompendiumForm
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingFromCompendiumPlayerData
import cz.frantisekmasa.wfrp_master.common.character.trappings.trappingIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddTrappingScreen(
    private val characterId: CharacterId,
    private val containerId: Uuid?,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTrappingScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::saveTrapping,
            detailScreenFactory = { CharacterTrappingDetailScreen(characterId, it.id) },
        )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.trappings_title_choose_compendium_trapping),
                    onDismissRequest = navigation::goBack,
                    icon = { trappingIcon(it.trappingType) },
                    onSelect = addItemUiState::openSpecificationScreen,
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.trappings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.TrappingContainer,
                )
            },
            specification = { compendiumTrapping ->
                TrappingFromCompendiumForm(
                    itemName = compendiumTrapping.name,
                    data = TrappingFromCompendiumPlayerData(
                        itemQualities = emptySet(),
                        itemFlaws = emptySet(),
                        quantity = 1,
                        note = "",
                    ),
                    onSaveRequest = {
                        val item = InventoryItem.fromCompendium(
                            compendiumTrapping,
                            it.itemQualities,
                            it.itemFlaws,
                            it.quantity,
                            it.note,
                        )

                        addItemUiState.saveItem(
                            if (containerId != null)
                                item.addToContainer(containerId)
                            else item
                        )
                    },
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            },
            nonCompendiumItemForm = {
                NonCompendiumTrappingForm(
                    existingItem = null,
                    onSaveRequest = addItemUiState::saveItem,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    defaultContainerId = null,
                )
            }
        )
    }
}
