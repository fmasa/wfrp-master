package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun EditSpellDialog(
    screenModel: SpellsScreenModel,
    spellId: Uuid,
    onDismissRequest: () -> Unit
) {
    val spell = screenModel.items.collectWithLifecycle(null)
        .value
        ?.firstOrNull { it.id == spellId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (spell.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            SpellDetail(
                spell = spell,
                onDismissRequest = onDismissRequest,
                onMemorizedChange = { memorized ->
                    coroutineScope.launch(Dispatchers.IO) {
                        screenModel.saveSpell(spell.copy(memorized = memorized))
                    }
                }
            )
        } else {
            NonCompendiumSpellForm(
                screenModel = screenModel,
                existingSpell = spell,
                onDismissRequest = onDismissRequest
            )
        }
    }
}
