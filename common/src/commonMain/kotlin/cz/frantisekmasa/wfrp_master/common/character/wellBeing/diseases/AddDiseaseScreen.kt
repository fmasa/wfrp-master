package cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.diseases.CharacterDiseaseDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.diseases.NonCompendiumDiseaseForm
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItems
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease as CompendiumDisease

data class AddDiseaseScreen(
    private val characterId: CharacterId,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: AddDiseaseScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val addItemUiState =
            rememberAddItemUiState(
                saver = screenModel::addDisease,
                detailScreenFactory = { CharacterDiseaseDetailScreen(characterId, it.id) },
            )

        val isGameMaster = state.availableCompendiumItems.isGameMaster

        AddItemUi(
            state = addItemUiState,
            chooser = {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.diseases_title_choose_compendium_disease),
                    onDismissRequest = navigation::goBack,
                    icon = { Resources.Drawable.Disease },
                    onSelect = addItemUiState::openSpecificationScreen,
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.diseases_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Disease,
                )
            },
            specification = { compendiumDisease ->
                DiseaseSpecificationForm(
                    existingDisease = null,
                    compendiumDisease = compendiumDisease,
                    onSave = {
                        screenModel.addDisease(
                            Disease.fromCompendium(
                                compendiumDisease = compendiumDisease,
                                isDiagnosed = it.isDiagnosed,
                                incubation = it.incubation,
                                duration = it.duration,
                            ),
                        )
                    },
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    isGameMaster = isGameMaster,
                )
            },
            nonCompendiumItemForm = {
                NonCompendiumDiseaseForm(
                    existingDisease = null,
                    onSave = screenModel::addDisease,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    isGameMaster = isGameMaster,
                )
            },
        )
    }
}

data class AddDiseaseScreenState(
    val availableCompendiumItems: AvailableCompendiumItems<CompendiumDisease>,
)
