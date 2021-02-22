package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.annotation.MainThread
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
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
    var screen by remember(state) { mutableStateOf(ItemsScreen.SKILLS) }

    when (screen) {
        ItemsScreen.SKILLS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_skills),
                items = state.skills,
                onSave = viewModel::saveMultipleSkills,
                onContinue = { screen = ItemsScreen.TALENTS },
                onClose = onDismissRequest,
                existingItems = viewModel.skills,
            )
        }
        ItemsScreen.TALENTS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_talents),
                items = state.talents,
                onSave = viewModel::saveMultipleTalents,
                onContinue = { screen = ItemsScreen.SPELLS },
                onClose = onDismissRequest,
                existingItems = viewModel.talents,
            )
        }
        ItemsScreen.SPELLS -> {
            ItemPicker(
                label = stringResource(R.string.rulebook_import_pick_spells),
                items = state.spells,
                onSave = viewModel::saveMultipleSpells,
                onContinue = onComplete,
                onClose = onDismissRequest,
                existingItems = viewModel.spells,
            )
        }
    }
}

@Composable
private fun <T : CompendiumItem> ItemPicker(
    label: String,
    onSave: suspend (items: List<T>) -> Unit,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    existingItems: LiveData<List<T>>,
    items: List<T>,
) {
    val existingItemsList = existingItems.observeAsState().value
    val existingItemNames = remember(existingItemsList) {
        existingItemsList?.map { it.name }?.toHashSet() ?: emptySet()
    }

    val selectedItems = remember(items, existingItemNames) {
        if (existingItemsList == null) {
            return@remember mutableStateMapOf()
        }

        items.map { it.id to !existingItemNames.contains(it.name) }.toMutableStateMap()
    }
    val atLeastOneSelected = selectedItems.containsValue(true)

    var saving by remember { mutableStateOf(false) }
    val isLoading = saving || existingItemsList == null

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onClose) },
                title = { Text(stringResource(R.string.title_importing_rulebook)) },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    TopBarAction(
                        textRes = if (atLeastOneSelected) R.string.button_save else R.string.button_skip,
                        enabled = !isLoading,
                        onClick = {
                            coroutineScope.launch {
                                saving = true

                                if (atLeastOneSelected) {
                                    withContext(Dispatchers.IO) {
                                        onSave(items.filter { selectedItems.contains(it.id) })
                                    }
                                }

                                withContext(Dispatchers.Main) { onContinue() }
                                saving = false
                            }
                        }
                    )
                }
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
                return@Column
            }

            LazyColumn {
                items(items) { item ->
                    ListItem(
                        icon = {
                            Checkbox(
                                checked = selectedItems[item.id] ?: false,
                                onCheckedChange = { selectedItems[item.id] = it },
                            )
                        },
                        modifier = Modifier.toggleable(
                            value = selectedItems[item.id] ?: false,
                            onValueChange = { selectedItems[item.id] = it },
                        ),
                        text = { Text(item.name) },
                        secondaryText = if (existingItemNames.contains(item.name)) {
                            { Text(stringResource(R.string.item_with_name_exists)) }
                        } else null
                    )
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