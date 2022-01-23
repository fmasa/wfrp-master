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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    val strings = LocalStrings.current.compendium

    when (screen) {
        ItemsScreen.SKILLS -> {
            ItemPicker(
                label = strings.pickPromptSkills,
                items = state.skills,
                onSave = viewModel::saveMultipleSkills,
                onContinue = { screen = ItemsScreen.TALENTS },
                onClose = onDismissRequest,
                existingItems = viewModel.skills,
            )
        }
        ItemsScreen.TALENTS -> {
            ItemPicker(
                label =  strings.pickPromptTalents,
                items = state.talents,
                onSave = viewModel::saveMultipleTalents,
                onContinue = { screen = ItemsScreen.SPELLS },
                onClose = onDismissRequest,
                existingItems = viewModel.talents,
            )
        }
        ItemsScreen.SPELLS -> {
            ItemPicker(
                label =  strings.pickPromptSpells,
                items = state.spells,
                onSave = viewModel::saveMultipleSpells,
                onContinue = { screen = ItemsScreen.BLESSINGS },
                onClose = onDismissRequest,
                existingItems = viewModel.spells,
            )
        }
        ItemsScreen.BLESSINGS -> {
            ItemPicker(
                label =  strings.pickPromptBlessings,
                items = state.blessings,
                onSave = viewModel::saveMultipleBlessings,
                onContinue = { screen = ItemsScreen.MIRACLES },
                onClose = onDismissRequest,
                existingItems = viewModel.blessings,
            )
        }
        ItemsScreen.MIRACLES -> {
            ItemPicker(
                label =  strings.pickPromptMiracles,
                items = state.miracles,
                onSave = viewModel::saveMultipleMiracles,
                onContinue = onComplete,
                onClose = onDismissRequest,
                existingItems = viewModel.miracles,
            )
        }
    }
}

@Composable
private fun <T : CompendiumItem<T>> ItemPicker(
    label: String,
    onSave: suspend (items: List<T>) -> Unit,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    existingItems: Flow<List<T>>,
    items: List<T>,
) {
    val existingItemsList = existingItems.collectWithLifecycle(null).value

    if (existingItemsList == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClose) },
                    title = { Text(LocalStrings.current.compendium.titleImportDialog) }
                )
            },
            content = { FullScreenProgress() }
        )
        return
    }

    val existingItemNames = remember(existingItemsList) {
        existingItemsList.map { it.name }.toHashSet()
    }

    val selectedItems = remember(items, existingItemNames) {
        items.map { it.id to !existingItemNames.contains(it.name) }.toMutableStateMap()
    }
    val atLeastOneSelected = selectedItems.containsValue(true)

    var saving by remember { mutableStateOf(false) }
    val isLoading = saving

    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onClose) },
                title = { Text(strings.compendium.titleImportDialog) },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    TopBarAction(
                        text = when {
                            atLeastOneSelected -> strings.commonUi.buttonSave
                            else -> strings.commonUi.buttonSkip
                        },
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
            } else {
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
                                { Text(strings.compendium.messages.itemAlreadyExists) }
                            } else null
                        )
                    }
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
        val blessings: List<Blessing>,
        val miracles: List<Miracle>,
    ) : ImportDialogState()
}

private enum class ItemsScreen {
    SKILLS,
    TALENTS,
    SPELLS,
    BLESSINGS,
    MIRACLES
}
