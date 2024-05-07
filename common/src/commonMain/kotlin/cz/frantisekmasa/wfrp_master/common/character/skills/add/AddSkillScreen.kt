package cz.frantisekmasa.wfrp_master.common.character.skills.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.items.AddItemUi
import cz.frantisekmasa.wfrp_master.common.character.items.rememberAddItemUiState
import cz.frantisekmasa.wfrp_master.common.character.skills.CharacterSkillDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.AdvancesForm
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.NonCompendiumSkillForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddSkillScreen(
    private val characterId: CharacterId,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: AddSkillScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        val navigation = LocalNavigationTransaction.current

        val addItemUiState =
            rememberAddItemUiState(
                saver = screenModel::addSkill,
                detailScreenFactory = { CharacterSkillDetailScreen(characterId, it.id) },
            )

        AddItemUi(
            state = addItemUiState,
            chooser = {
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.skills_title_choose_compendium_skill),
                    onDismissRequest = navigation::goBack,
                    icon = { it.characteristic.getIcon() },
                    onSelect = addItemUiState::openSpecificationScreen,
                    onCustomItemRequest = addItemUiState::openNonCompendiumItemForm,
                    customItemButtonText = stringResource(Str.skills_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Skill,
                )
            },
            specification = { compendiumSkill ->
                AdvancesForm(
                    compendiumSkill = compendiumSkill,
                    characteristics = state.characteristics,
                    isAdvanced = compendiumSkill.advanced,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                    onSave = {
                        addItemUiState.saveItem(
                            Skill.fromCompendium(compendiumSkill, advances = it),
                        )
                    },
                )
            },
            nonCompendiumItemForm = {
                NonCompendiumSkillForm(
                    onSave = addItemUiState::saveItem,
                    existingSkill = null,
                    characteristics = state.characteristics,
                    onDismissRequest = addItemUiState::openChoosingScreen,
                )
            },
        )
    }
}
