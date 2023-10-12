package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.CharacterBlessingDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog.NonCompendiumBlessingForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddBlessingScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddBlessingScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::addBlessing,
            detailScreenFactory = { CharacterBlessingDetailScreen(characterId, it.id) },
        )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.blessings_title_choose_compendium_blessing),
                    onDismissRequest = navigation::goBack,
                    icon = { Resources.Drawable.Blessing },
                    onSelect = { addItemUiState.saveItem(Blessing.fromCompendium(it)) },
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.blessings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Blessing,
                )
            },
            specification = { error("There is no specification for blessings") },
            nonCompendiumItemForm = {
                NonCompendiumBlessingForm(
                    existingBlessing = null,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    onSave = screenModel::addBlessing,
                )
            }
        )
    }
}
