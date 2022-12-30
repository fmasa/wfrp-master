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
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel.ImportAction
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun ImportDialog(
    state: ImportDialogState,
    partyId: PartyId,
    screen: Screen,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        when (state) {
            ImportDialogState.LoadingItems -> Surface {
                FullScreenProgress()
            }
            is ImportDialogState.PickingItemsToImport -> ImportedItemsPicker(
                state = state,
                onDismissRequest = onDismissRequest,
                onComplete = onComplete,
                partyId = partyId,
                screen = screen,
            )
        }
    }
}

@Composable
private fun ImportedItemsPicker(
    partyId: PartyId,
    screen: Screen,
    state: ImportDialogState.PickingItemsToImport,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    var step by remember(state) { mutableStateOf(ItemsScreen.SKILLS) }

    val strings = LocalStrings.current.compendium

    when (step) {
        ItemsScreen.SKILLS -> {
            ItemPicker(
                label = strings.pickPromptSkills,
                items = state.skills,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.TALENTS },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.TALENTS -> {
            ItemPicker(
                label = strings.pickPromptTalents,
                items = state.talents,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.SPELLS },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.SPELLS -> {
            ItemPicker(
                label = strings.pickPromptSpells,
                items = state.spells,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.BLESSINGS },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.BLESSINGS -> {
            ItemPicker(
                label = strings.pickPromptBlessings,
                items = state.blessings,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.MIRACLES },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.MIRACLES -> {
            ItemPicker(
                label = strings.pickPromptMiracles,
                items = state.miracles,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.TRAITS },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.TRAITS -> {
            ItemPicker(
                label = strings.pickPromptTraits,
                items = state.traits,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = { step = ItemsScreen.CAREERS },
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
        ItemsScreen.CAREERS -> {
            ItemPicker(
                label = strings.pickPromptCareers,
                items = state.careers,
                screenModel = screen.rememberScreenModel(arg = partyId),
                onContinue = onComplete,
                onClose = onDismissRequest,
                replaceExistingByDefault = state.replaceExistingByDefault,
            )
        }
    }
}

@Composable
private fun <T : CompendiumItem<T>> ItemPicker(
    label: String,
    screenModel: CompendiumItemScreenModel<T, *>,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    items: List<T>,
    replaceExistingByDefault: Boolean,
) {
    val existingItemsList = screenModel.items.collectWithLifecycle(null).value

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
                                        screenModel.import(
                                            items
                                                .asSequence()
                                                .filter { selectedItems[it.id] == true }
                                                .map {
                                                    val existingItem = existingItemsByName[it.name]

                                                    if (existingItem != null)
                                                        ImportAction.Update(it.replace(existingItem))
                                                    else ImportAction.CreateNew(it)
                                                }
                                                .distinctBy { it.item.id }
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
