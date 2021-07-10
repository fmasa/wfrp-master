package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditSpellDialog(
    viewModel: SpellsViewModel,
    spellId: UUID,
    onDismissRequest: () -> Unit
) {
    val spell = viewModel.spells.observeAsState().value?.firstOrNull { it.id == spellId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (spell.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            SpellDetail(
                spell = spell,
                onDismissRequest = onDismissRequest,
                onMemorizedChange = { memorized ->
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.saveSpell(spell.copy(memorized = memorized))
                    }
                }
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
