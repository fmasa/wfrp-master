package cz.frantisekmasa.wfrp_master.common.character.spells.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
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

        val (step, setStep) = rememberSaveable { mutableStateOf(Step.ChoosingCompendiumSpell) }

        if (state == null) {
            FullScreenProgress()
            return
        }

        when (step) {
            Step.ChoosingCompendiumSpell -> {
                val navigation = LocalNavigationTransaction.current

                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.spells_title_choose_compendium_spell),
                    onDismissRequest = navigation::goBack,
                    customIcon = { SpellLoreIcon(it.lore) },
                    onSelect = { screenModel.saveItem(Spell.fromCompendium(it)) },
                    onCustomItemRequest = { setStep(Step.FillingInCustomSpell) },
                    customItemButtonText = stringResource(Str.spells_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Spell,
                )
            }
            Step.FillingInCustomSpell -> NonCompendiumSpellForm(
                onSave = screenModel::saveItem,
                existingSpell = null,
                onDismissRequest = { setStep(Step.ChoosingCompendiumSpell) },
            )
        }
    }

    private enum class Step {
        ChoosingCompendiumSpell,
        FillingInCustomSpell,
    }
}
