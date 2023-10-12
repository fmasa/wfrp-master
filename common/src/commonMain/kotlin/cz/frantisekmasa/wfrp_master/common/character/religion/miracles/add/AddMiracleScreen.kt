package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.CharacterMiracleDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog.NonCompendiumMiracleForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddMiracleScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddMiracleScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::addMiracle,
            detailScreenFactory = { CharacterMiracleDetailScreen(characterId, it.id) },
        )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.miracles_title_choose_compendium_miracle),
                    onDismissRequest = navigation::goBack,
                    icon = { Resources.Drawable.Miracle },
                    onSelect = { addItemUiState.saveItem(Miracle.fromCompendium(it)) },
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.miracles_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Miracle,
                )
            },
            specification = { error("There is no specification for miracles") },
            nonCompendiumItemForm = {
                NonCompendiumMiracleForm(
                    existingMiracle = null,
                    onSave = screenModel::addMiracle,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            }
        )
    }
}
