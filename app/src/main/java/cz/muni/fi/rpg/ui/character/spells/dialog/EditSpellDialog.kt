package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SpellsViewModel

@Composable
fun EditSpellDialog(
    viewModel: SpellsViewModel,
    spell: Spell,
    onDismissRequest: () -> Unit
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (spell.compendiumId != null) {
            SpellDetail(
                spell = spell,
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumSpellForm(
                viewModel = viewModel,
                existingSpell = spell,
                onDismissRequest = onDismissRequest
            )
        }
    }
}
