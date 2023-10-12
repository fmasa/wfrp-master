package cz.frantisekmasa.wfrp_master.common.character.traits.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.traits.CharacterTraitDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.TraitSpecificationsForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddTraitScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTraitScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState = rememberAddItemUiState(
            saver = screenModel::saveNewTrait,
            detailScreenFactory = { CharacterTraitDetailScreen(characterId, it.id) }
        )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.traits_title_choose_compendium_trait),
                    onDismissRequest = { navigation.goBack() },
                    icon = { Resources.Drawable.Trait },
                    onSelect = {
                        if (it.specifications.isEmpty()) {
                            addItemUiState.saveItem(Trait.fromCompendium(it, emptyMap()))
                        } else {
                            addItemUiState.openSpecificationScreen(it)
                        }
                    },
                    emptyUiIcon = Resources.Drawable.Trait,
                )
            },
            specification = { compendiumTrait ->
                TraitSpecificationsForm(
                    existingTrait = null,
                    onSave = { screenModel.saveNewTrait(Trait.fromCompendium(compendiumTrait, it)) },
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    defaultSpecifications = compendiumTrait.specifications.associateWith { "" },
                )
            },
            nonCompendiumItemForm = { error("There is no support for non-compendium Trait") },
        )
    }
}
