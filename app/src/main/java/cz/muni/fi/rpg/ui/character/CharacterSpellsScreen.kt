package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.character.spells.dialog.AddSpellDialog
import cz.muni.fi.rpg.ui.character.spells.dialog.EditSpellDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterSpellsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }
    var showAddSpellDialog by savedInstanceState { false }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSpellDialog = true }
            ) {
                Icon(vectorResource(R.drawable.ic_add))
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
    val spells = viewModel.spells.collectAsState(null).value ?: return

    if (spells.isEmpty()) {
        EmptyUI(
            textId = R.string.no_spells,
            subTextId = R.string.no_spells_sub_text,
            drawableResourceId = R.drawable.ic_spells)
        return
    }

    var editedSpell: Spell? by savedInstanceState { null }

    LazyColumnFor(spells, Modifier.padding(top = 12.dp)) { spell ->
        SpellItem(
            spell,
            onClick = { editedSpell = spell },
            onRemove = { viewModel.removeSpell(spell) }
        )
    }

    editedSpell?.let {
        EditSpellDialog(
            viewModel = viewModel,
            spell = it,
            onDismissRequest = { editedSpell = null },
        )
    }
}

@Composable
private fun SpellItem(spell: Spell, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = spell.name,
        description = spell.effect,
        iconRes = R.drawable.ic_spells,
        onClick = onClick,
        contextMenuItems = listOf(ContextMenu.Item(stringResource(R.string.remove), onRemove)),
        badge = {
            Row {
                Text(stringResource(R.string.spell_casting_number_shortcut))
                Text(spell.castingNumber.toString(), Modifier.padding(start = 4.dp))
            }
        }
    )
}