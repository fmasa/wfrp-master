package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellLoreIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun AddSpellDialog(screenModel: SpellsScreenModel, onDismissRequest: () -> Unit) {
    var state: AddSpellDialogState by rememberSaveable { mutableStateOf(AddSpellDialogState.ChoosingCompendiumSpell) }

    FullScreenDialog(
        onDismissRequest = {
            if (state != AddSpellDialogState.ChoosingCompendiumSpell) {
                state = AddSpellDialogState.ChoosingCompendiumSpell
            } else {
                onDismissRequest()
            }
        }
    ) {
        when (state) {
            AddSpellDialogState.ChoosingCompendiumSpell ->
                CompendiumItemChooser(
                    screenModel = screenModel,
                    title = LocalStrings.current.spells.titleChooseCompendiumSpell,
                    onDismissRequest = onDismissRequest,
                    customIcon = { SpellLoreIcon(it.lore) },
                    onSelect = { screenModel.saveItem(Spell.fromCompendium(it)) },
                    onCustomItemRequest = { state = AddSpellDialogState.FillingInCustomSpell },
                    customItemButtonText = LocalStrings.current.spells.buttonAddNonCompendium,
                    emptyUiIcon = Resources.Drawable.Spell,
                )
            AddSpellDialogState.FillingInCustomSpell -> NonCompendiumSpellForm(
                screenModel = screenModel,
                existingSpell = null,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private enum class AddSpellDialogState {
    ChoosingCompendiumSpell,
    FillingInCustomSpell,
}
