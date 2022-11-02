package cz.frantisekmasa.wfrp_master.common.compendium

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
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun ImportDialog(
    screenModel: CompendiumScreenModel,
    state: ImportDialogState,
    partyId: PartyId,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        when (state) {
            ImportDialogState.LoadingItems -> Surface {
                FullScreenProgress()
            }
            is ImportDialogState.PickingItemsToImport -> ImportedItemsPicker(
                screenModel = screenModel,
                state = state,
                onDismissRequest = onDismissRequest,
                onComplete = onComplete,
            )
        }
    }
}

@Composable
private fun ImportedItemsPicker(
    screenModel: CompendiumScreenModel,
    state: ImportDialogState.PickingItemsToImport,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    var screen by remember(state) { mutableStateOf(ItemsScreen.SKILLS) }

    val strings = LocalStrings.current.compendium

    when (screen) {
        ItemsScreen.SKILLS -> {
            ItemPicker(
                label = strings.pickPromptSkills,
                items = state.skills,
                onSave = screenModel::saveMultipleSkills,
                onContinue = { screen = ItemsScreen.TALENTS },
                onClose = onDismissRequest,
                existingItems = screenModel.skills,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.TALENTS -> {
            ItemPicker(
                label = strings.pickPromptTalents,
                items = state.talents,
                onSave = screenModel::saveMultipleTalents,
                onContinue = { screen = ItemsScreen.SPELLS },
                onClose = onDismissRequest,
                existingItems = screenModel.talents,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.SPELLS -> {
            ItemPicker(
                label = strings.pickPromptSpells,
                items = state.spells,
                onSave = screenModel::saveMultipleSpells,
                onContinue = { screen = ItemsScreen.BLESSINGS },
                onClose = onDismissRequest,
                existingItems = screenModel.spells,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.BLESSINGS -> {
            ItemPicker(
                label = strings.pickPromptBlessings,
                items = state.blessings,
                onSave = screenModel::saveMultipleBlessings,
                onContinue = { screen = ItemsScreen.MIRACLES },
                onClose = onDismissRequest,
                existingItems = screenModel.blessings,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.MIRACLES -> {
            ItemPicker(
                label = strings.pickPromptMiracles,
                items = state.miracles,
                onSave = screenModel::saveMultipleMiracles,
                onContinue = { screen = ItemsScreen.TRAITS },
                onClose = onDismissRequest,
                existingItems = screenModel.miracles,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.TRAITS -> {
            ItemPicker(
                label = strings.pickPromptTraits,
                items = state.traits,
                onSave = screenModel::saveMultipleTraits,
                onContinue = { screen = ItemsScreen.CAREERS },
                onClose = onDismissRequest,
                existingItems = screenModel.traits,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.CAREERS -> {
            ItemPicker(
                label = strings.pickPromptCareers,
                items = state.careers,
                onSave = screenModel::saveMultipleCareers,
                onContinue = onComplete,
                onClose = onDismissRequest,
                existingItems = screenModel.careers,
                replaceExistingByDefault = state.replaceExistingByDefault,
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
    replaceExistingByDefault: Boolean,
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

    val existingItemsByName = remember(existingItemsList) {
        existingItemsList.associateBy { it.name }
    }

    val selectedItems = remember(items, existingItemsByName, replaceExistingByDefault) {
        items.map { it.id to (replaceExistingByDefault || it.name !in existingItemsByName) }
            .toMutableStateMap()
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
                                        onSave(
                                            items
                                                .asSequence()
                                                .filter { selectedItems[it.id] == true }
                                                .map {
                                                    val existingItem = existingItemsByName[it.name]

                                                    if (existingItem != null)
                                                        it.replace(existingItem)
                                                    else it
                                                }
                                                .distinctBy { it.id }
                                                .toList()
                                        )
                                    }
                                }

                                onContinue()
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
                            secondaryText = if (item.name in existingItemsByName) {
                                {
                                    Text(
                                        if (selectedItems[item.id] == true)
                                            strings.compendium.messages.willReplaceExistingItem
                                        else strings.compendium.messages.itemAlreadyExists
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Immutable
sealed class ImportDialogState {
    @Immutable
    object LoadingItems : ImportDialogState()

    @Immutable
    data class PickingItemsToImport(
        val skills: List<Skill>,
        val talents: List<Talent>,
        val spells: List<Spell>,
        val blessings: List<Blessing>,
        val miracles: List<Miracle>,
        val traits: List<Trait>,
        val careers: List<Career>,
        val replaceExistingByDefault: Boolean,
    ) : ImportDialogState()
}

@Immutable
private enum class ItemsScreen {
    SKILLS,
    TALENTS,
    SPELLS,
    BLESSINGS,
    MIRACLES,
    TRAITS,
    CAREERS,
}
