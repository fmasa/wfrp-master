package cz.frantisekmasa.wfrp_master.common.character.spells.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.spells.CharacterSpellDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.NonCompendiumSpellForm
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellLoreIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddSpellScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddSpellScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::saveItem,
            detailScreenFactory = { CharacterSpellDetailScreen(characterId, it.id) },
        )
        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.spells_title_choose_compendium_spell),
                    onDismissRequest = navigation::goBack,
                    customIcon = { SpellLoreIcon(it.lore) },
                    onSelect = { addItemUiState.saveItem(Spell.fromCompendium(it)) },
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.spells_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Spell,
                )
            },
            specification = {
                TODO("Implement specification form with 'memorized' checkbox")
            },
            nonCompendiumItemForm = {
                NonCompendiumSpellForm(
                    onSave = addItemUiState::saveItem,
                    existingSpell = null,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            }
        )
    }
}
