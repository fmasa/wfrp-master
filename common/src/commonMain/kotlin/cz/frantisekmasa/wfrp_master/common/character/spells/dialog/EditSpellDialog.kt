package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
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
                subheadBar = {
                    SubheadBar {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(LocalStrings.current.spells.labelMemorized)
                            Switch(
                                checked = spell.memorized,
                                onCheckedChange = { memorized ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        screenModel.saveSpell(spell.copy(memorized = memorized))
                                    }
                                },
                            )
                        }
                    }
                },
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
