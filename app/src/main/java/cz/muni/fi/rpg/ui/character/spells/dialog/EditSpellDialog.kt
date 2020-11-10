package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
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
                existingSpell = spell,
                compendiumSpellId = spell.compendiumId,
                viewModel = viewModel,
            )
        } else {
            NonCompendiumSpellForm(
                viewModel = viewModel,
                existingSpell = spell,
                onComplete = onDismissRequest
            )
        }
    }
}
