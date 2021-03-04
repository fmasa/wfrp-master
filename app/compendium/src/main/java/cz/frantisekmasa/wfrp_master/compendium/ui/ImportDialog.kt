package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.annotation.MainThread
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf


@Composable
internal fun ImportDialog(
    state: ImportDialogState,
    partyId: PartyId,
    onDismissRequest: () -> Unit,
    @MainThread onComplete: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        when (state) {
            ImportDialogState.LoadingItems -> Surface {
                FullScreenProgress()
            }
            is ImportDialogState.PickingItemsToImport -> ImportedItemsPicker(
                state = state,
                partyId = partyId,
                onDismissRequest = onDismissRequest,
                onComplete = onComplete,
            )
        }
    }
}

@Composable
private fun ImportedItemsPicker(
    partyId: PartyId,
    state: ImportDialogState.PickingItemsToImport,
    onDismissRequest: () -> Unit,
    @MainThread onComplete: () -> Unit,
) {
    val viewModel: CompendiumViewModel by viewModel { parametersOf(partyId) }

    var saving by remember { mutableStateOf(false) }
    var screen by remember(state) { mutableStateOf(ItemsScreen.SKILLS) }

    val selectedItems = remember(state) {
        (state.skills.map { it.id to true } +
            state.talents.map { it.id to true } +
            state.spells.map { it.id to true }).toMutableStateMap()
    }

    val coroutineScope = rememberCoroutineScope()

    val save: (suspend CoroutineScope.() -> Unit) -> Unit = { action ->
        coroutineScope.launch {
            saving = true

            withContext(Dispatchers.IO, action)

            saving = false
        }
    }

    when (screen) {
        ItemsScreen.SKILLS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_skills),
                items = state.skills,
                isLoading = saving,
                onSave = {
                    save {
                        viewModel.saveMultipleSkills(
                            state.skills.filter { selectedItems[it.id] ?: false }
                        )

                        screen = ItemsScreen.TALENTS
                    }
                },
                onClose = onDismissRequest,
            ) { skill ->
                ListItem(
                    icon = {
                        Checkbox(
                            checked = selectedItems[skill.id] ?: false,
                            onCheckedChange = { selectedItems[skill.id] = it },
                        )
                    },
                    modifier = Modifier.toggleable(
                        value = selectedItems[skill.id] ?: false,
                        onValueChange = { selectedItems[skill.id] = it },
                    )
                ) {
                    Text(skill.name)
                }
            }
        }
        ItemsScreen.TALENTS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_talents),
                items = state.talents,
                isLoading = saving,
                onSave = {
                    save {
                        viewModel.saveMultipleTalents(
                            state.talents.filter { selectedItems[it.id] ?: false }
                        )

                        screen = ItemsScreen.SPELLS
                    }
                },
                onClose = onDismissRequest,
            ) { talent ->
                ListItem(
                    icon = {
                        Checkbox(
                            checked = selectedItems[talent.id] ?: false,
                            onCheckedChange = { selectedItems[talent.id] = it },
                        )
                    },
                    modifier = Modifier.toggleable(
                        value = selectedItems[talent.id] ?: false,
                        onValueChange = { selectedItems[talent.id] = it },
                    )
                ) {
                    Text(talent.name)
                }
            }
        }
        ItemsScreen.SPELLS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_spells),
                items = state.spells,
                isLoading = saving,
                onSave = {
                    save {
                        viewModel.saveMultipleSpells(
                            state.spells.filter { selectedItems[it.id] ?: false }
                        )

                        withContext(Dispatchers.Main) {
                            onComplete()
                        }
                    }
                },
                onClose = onDismissRequest,
            ) { spell ->
                ListItem(
                    icon = {
                        Checkbox(
                            checked = selectedItems[spell.id] ?: false,
                            onCheckedChange = { selectedItems[spell.id] = it },
                        )
                    },
                    modifier = Modifier.toggleable(
                        value = selectedItems[spell.id] ?: false,
                        onValueChange = { selectedItems[spell.id] = it },
                    ),
                    text = { Text(spell.name) },
                )
            }
        }
    }
}

@Composable
private fun <T : CompendiumItem> ItemPicker(
    label: String,
    isLoading: Boolean,
    onSave: () -> Unit,
    onClose: () -> Unit,
    items: List<T>,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onClose) },
                title = { Text(stringResource(R.string.title_importing_rulebook)) },
                actions = { SaveAction(enabled = !isLoading, onClick = onSave) }
            )
        },
    ) {
        Column(Modifier.fillMaxWidth()) {
            Surface(elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    label,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            if (isLoading) {
                FullScreenProgress()
            } else {
                LazyColumn {
                    items(items, itemContent = itemContent)
                }
            }
        }
    }
}

@Immutable
internal sealed class ImportDialogState {
    object LoadingItems : ImportDialogState()
    data class PickingItemsToImport(
        val skills: List<Skill>,
        val talents: List<Talent>,
        val spells: List<Spell>,
    ) : ImportDialogState()
}

private enum class ItemsScreen {
    SKILLS,
    TALENTS,
    SPELLS,
}