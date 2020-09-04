package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.character.spells.SpellDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterSpellsFragment : Fragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSpellsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)

    private val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = { openSpellDialog(null) }) {
                                Icon(vectorResource(R.drawable.ic_add))
                            }
                        }
                    ) {
                        MainContainer(
                            viewModel,
                            onSpellClick = { openSpellDialog(it) },
                            onSpellRemove = { launch { viewModel.removeSpell(it) } }
                        )
                    }
                }
            }
        }
    }

    private fun openSpellDialog(existingSpell: Spell?) {
        SpellDialog.newInstance(characterId, existingSpell)
            .show(childFragmentManager, "SpellDialog")
    }
}

@Composable
private fun MainContainer(
    viewModel: SpellsViewModel,
    onSpellClick: (Spell) -> Unit,
    onSpellRemove: (Spell) -> Unit
) {
    val spells = viewModel.spells.observeAsState().value ?: return

    if (spells.isEmpty()) {
        EmptyUI(R.string.no_spells, R.drawable.ic_spells)
        return
    }

    LazyColumnFor(spells, Modifier.padding(top = 12.dp)) { spell ->
        SpellItem(spell, onClick = { onSpellClick(spell) }, onRemove = { onSpellRemove(spell) })
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