package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.character.spells.SpellDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterSpellsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }
    val fragmentManager = fragmentManager()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { SpellDialog.newInstance(characterId, null).show(fragmentManager, null) }
            ) {
                Icon(vectorResource(R.drawable.ic_add))
            }
        }
    ) {
        MainContainer(
            viewModel,
            onSpellClick = { SpellDialog.newInstance(characterId, it).show(fragmentManager, null) }
        )
    }
}

@Composable
private fun MainContainer(viewModel: SpellsViewModel, onSpellClick: (Spell) -> Unit) {
    val spells = viewModel.spells.observeAsState().value ?: return

    if (spells.isEmpty()) {
        EmptyUI(
            textId = R.string.no_spells,
            subTextId = R.string.no_spells_sub_text,
            drawableResourceId = R.drawable.ic_spells)
        return
    }

    LazyColumnFor(spells, Modifier.padding(top = 12.dp)) { spell ->
        SpellItem(
            spell,
            onClick = { onSpellClick(spell) },
            onRemove = { viewModel.removeSpell(spell) }
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
        badgeContent = {
            Row {
                Text(stringResource(R.string.spell_casting_number_shortcut))
                Text(spell.castingNumber.toString(), Modifier.padding(start = 4.dp))
            }
        }
    )
}