package cz.frantisekmasa.wfrp_master.common.character.talents.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.talents.CharacterTalentDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.NonCompendiumTalentForm
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TimesTakenForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddTalentScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTalentScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::addTalent,
            detailScreenFactory = { CharacterTalentDetailScreen(characterId, it.id) },
        )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.talents_title_choose_compendium_talent),
                    onDismissRequest = navigation::goBack,
                    icon = { Resources.Drawable.Talent },
                    onSelect = addItemUiState::openSpecificationScreen,
                    onCustomItemRequest = { addItemUiState.openNonCompendiumItemForm() },
                    customItemButtonText = stringResource(Str.talents_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Talent,
                )
            },
            specification = { compendiumTalent ->
                TimesTakenForm(
                    existingTalent = null,
                    onSave = {
                        addItemUiState.saveItem(
                            Talent.fromCompendium(compendiumTalent, timesTaken = it)
                        )
                    },
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            },
            nonCompendiumItemForm = {
                NonCompendiumTalentForm(
                    existingTalent = null,
                    onSave = screenModel::addTalent,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            }
        )
    }
}
