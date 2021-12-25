package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.character.spells.dialog.AddSpellDialog
import cz.muni.fi.rpg.ui.character.spells.dialog.EditSpellDialog
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
internal fun CharacterSpellsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }
    var showAddSpellDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSpellDialog = true }) {
                Icon(
                    Icons.Rounded.Add,
                    stringResource(R.string.icon_add_spell),
                )
            }
        }
    ) {
        MainContainer(viewModel)

        if (showAddSpellDialog) {
            AddSpellDialog(
                viewModel = viewModel,
                onDismissRequest = { showAddSpellDialog = false },
            )
        }
    }
}

@Composable
private fun MainContainer(viewModel: SpellsViewModel) {
    val spells = viewModel.spells.collectWithLifecycle(null).value ?: return

    if (spells.isEmpty()) {
        EmptyUI(
            textId = R.string.no_spells,
            subTextId = R.string.no_spells_sub_text,
            icon = Resources.Drawable.Spell,
        )
        return
    }

    var editedSpellId: UUID? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(contentPadding = PaddingValues(bottom = Spacing.bottomPaddingUnderFab)) {
        items(spells) { spell ->
            SpellItem(
                spell,
                onClick = { editedSpellId = spell.id },
                onRemove = { viewModel.removeSpell(spell) }
            )
        }
    }

    editedSpellId?.let {
        EditSpellDialog(
            viewModel = viewModel,
            spellId = it,
            onDismissRequest = { editedSpellId = null },
        )
    }
}

@Composable
private fun SpellItem(spell: Spell, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = spell.name,
        description = spell.effect,
        icon = { ItemIcon(Resources.Drawable.Spell, ItemIcon.Size.Small) },
        onClick = onClick,
        contextMenuItems = listOf(ContextMenu.Item(stringResource(R.string.remove), onRemove)),
        badge = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.tiny),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Text(stringResource(R.string.spell_casting_number_shortcut))
                    Text(
                        spell.effectiveCastingNumber.toString(),
                        Modifier.padding(start = Spacing.tiny),
                    )
                }

                if (spell.memorized) {
                    Icon(
                        drawableResource(Resources.Drawable.MemorizeSpell),
                        stringResource(R.string.spell_memorized),
                        Modifier.size(16.dp),
                    )
                }
            }
        }
    )
}
