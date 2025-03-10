package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel.ImportAction
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.BlessingCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.career.CareerCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.disease.DiseaseCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.MiracleCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.skill.SkillCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.talent.TalentCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.trait.TraitCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.trapping.TrappingCompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TriStateCheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import dev.icerock.moko.resources.compose.stringResource
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
            ImportDialogState.LoadingItems ->
                Surface {
                    FullScreenProgress()
                }

            is ImportDialogState.PickingItemsToImport ->
                ImportedItemsPicker(
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
    val steps =
        remember(state) {
            sequenceOf(
                ItemsScreen.SKILLS to state.skills.isNotEmpty(),
                ItemsScreen.TALENTS to state.talents.isNotEmpty(),
                ItemsScreen.SPELLS to state.spells.isNotEmpty(),
                ItemsScreen.BLESSINGS to state.blessings.isNotEmpty(),
                ItemsScreen.MIRACLES to state.miracles.isNotEmpty(),
                ItemsScreen.TRAITS to state.traits.isNotEmpty(),
                ItemsScreen.CAREERS to state.careers.isNotEmpty(),
                ItemsScreen.TRAPPINGS to state.trappings.isNotEmpty(),
                ItemsScreen.DISEASES to state.diseases.isNotEmpty(),
                ItemsScreen.JOURNAL to state.journalEntries.isNotEmpty(),
            ).filter { it.second }
                .map { it.first }
                .toList()
        }
    var step by remember(state) { mutableStateOf(steps.firstOrNull() ?: ItemsScreen.SKILLS) }

    val onContinue = {
        val index = steps.indexOf(step)

        if (index == -1 || index == steps.lastIndex) {
            Reporting.record { compendiumImportFinished(state.source) }
            onComplete()
        } else {
            step = steps[index + 1]
        }
    }

    key(step) {
        when (step) {
            ItemsScreen.SKILLS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_skills),
                    items = state.skills,
                    screenModel =
                        screen.rememberScreenModel<PartyId, SkillCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.TALENTS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_talents),
                    items = state.talents,
                    screenModel =
                        screen.rememberScreenModel<PartyId, TalentCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.SPELLS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_spells),
                    items = state.spells,
                    screenModel =
                        screen.rememberScreenModel<PartyId, SpellCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.BLESSINGS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_blessings),
                    items = state.blessings,
                    screenModel =
                        screen.rememberScreenModel<PartyId, BlessingCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.MIRACLES -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_miracles),
                    items = state.miracles,
                    screenModel =
                        screen.rememberScreenModel<PartyId, MiracleCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.TRAITS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_traits),
                    items = state.traits,
                    screenModel =
                        screen.rememberScreenModel<PartyId, TraitCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.CAREERS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_careers),
                    items = state.careers,
                    screenModel =
                        screen.rememberScreenModel<PartyId, CareerCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.TRAPPINGS -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_trappings),
                    items = state.trappings,
                    screenModel =
                        screen.rememberScreenModel<PartyId, TrappingCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.DISEASES -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_diseases),
                    items = state.diseases,
                    screenModel =
                        screen.rememberScreenModel<PartyId, DiseaseCompendiumScreenModel>(
                            arg = partyId,
                        ),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }

            ItemsScreen.JOURNAL -> {
                ItemPicker(
                    label = stringResource(Str.compendium_pick_prompt_journal),
                    items = state.journalEntries,
                    screenModel = screen.rememberScreenModel<PartyId, JournalScreenModel>(arg = partyId),
                    onContinue = onContinue,
                    onClose = onDismissRequest,
                    overlineText = { it.parents.joinToString(" ${JournalEntry.PARENT_SEPARATOR} ") },
                    replaceExistingByDefault = state.replaceExistingByDefault,
                )
            }
        }
    }
}

@Composable
private fun <T : CompendiumItem<T>> ItemPicker(
    label: String,
    screenModel: CompendiumItemScreenModel<T>,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    items: List<T>,
    overlineText: ((T) -> String)? = null,
    replaceExistingByDefault: Boolean,
) {
    val existingItemsList = screenModel.items.collectWithLifecycle(null).value

    if (existingItemsList == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClose) },
                    title = { Text(stringResource(Str.compendium_title_import_dialog)) },
                )
            },
            content = { FullScreenProgress() },
        )
        return
    }

    val existingItemsByName =
        remember(existingItemsList) {
            existingItemsList.associateBy { it.name }
        }

    val (unchangedItems, changedItems) =
        remember(items, existingItemsByName) {
            items.partition {
                val existingItem = existingItemsByName[it.name]

                existingItem != null && it.replace(existingItem) == existingItem
            }
        }

    val selectedItems =
        remember(changedItems, existingItemsByName, replaceExistingByDefault) {
            changedItems.map { it.id to (replaceExistingByDefault || it.name !in existingItemsByName) }
                .toMutableStateMap()
        }
    val atLeastOneSelected = selectedItems.containsValue(true)

    var saving by remember { mutableStateOf(false) }
    val isLoading = saving

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onClose) },
                title = { Text(stringResource(Str.compendium_title_import_dialog)) },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    TopBarAction(
                        text =
                            stringResource(
                                when {
                                    atLeastOneSelected -> Str.common_ui_button_save
                                    else -> Str.common_ui_button_skip
                                },
                            ),
                        enabled = !isLoading,
                        onClick = {
                            coroutineScope.launch {
                                saving = true

                                if (atLeastOneSelected) {
                                    withContext(Dispatchers.IO) {
                                        screenModel.import(
                                            changedItems
                                                .asSequence()
                                                .filter { selectedItems[it.id] == true }
                                                .map {
                                                    val existingItem = existingItemsByName[it.name]

                                                    if (existingItem != null) {
                                                        ImportAction.Update(it.replace(existingItem))
                                                    } else {
                                                        ImportAction.CreateNew(it)
                                                    }
                                                }
                                                .distinctBy { it.item.id }
                                                .toList(),
                                        )
                                    }
                                }

                                onContinue()
                                saving = false
                            }
                        },
                    )
                },
            )
        },
    ) {
        Column(Modifier.fillMaxWidth()) {
            SubheadBar {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label, textAlign = TextAlign.Center)
                    Text(
                        stringResource(
                            Str.compendium_messages_unchanged_items,
                            unchangedItems.size,
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }

            if (isLoading) {
                FullScreenProgress()
            } else if (changedItems.isEmpty()) {
                EmptyUI(
                    text = stringResource(Str.compendium_messages_no_changed_items),
                    icon = Icons.Rounded.CheckCircle,
                )
            } else {
                LazyColumn {
                    item {
                        val checkboxState by remember {
                            derivedStateOf {
                                when {
                                    !selectedItems.containsValue(false) -> ToggleableState.On
                                    selectedItems.containsValue(true) -> ToggleableState.Indeterminate
                                    else -> ToggleableState.Off
                                }
                            }
                        }

                        TriStateCheckboxWithText(
                            text = stringResource(Str.common_ui_select_all),
                            state = checkboxState,
                            onClick = {
                                val shouldSelectAllItems = checkboxState == ToggleableState.Off
                                selectedItems.putAll(
                                    selectedItems.map { it.key to shouldSelectAllItems },
                                )
                            },
                        )
                    }
                    items(changedItems) { item ->
                        ListItem(
                            icon = {
                                Checkbox(
                                    checked = selectedItems[item.id] ?: false,
                                    onCheckedChange = { selectedItems[item.id] = it },
                                )
                            },
                            modifier =
                                Modifier.toggleable(
                                    value = selectedItems[item.id] ?: false,
                                    onValueChange = { selectedItems[item.id] = it },
                                ),
                            text = { Text(item.name) },
                            overlineText = overlineText?.let { { Text(it(item)) } },
                            secondaryText =
                                if (item.name in existingItemsByName) {
                                    {
                                        Text(
                                            stringResource(
                                                if (selectedItems[item.id] == true) {
                                                    Str.compendium_messages_will_replace_existing_item
                                                } else {
                                                    Str.compendium_messages_item_already_exists
                                                },
                                            ),
                                        )
                                    }
                                } else {
                                    null
                                },
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
        val source: String,
        val skills: List<Skill>,
        val talents: List<Talent>,
        val spells: List<Spell>,
        val blessings: List<Blessing>,
        val miracles: List<Miracle>,
        val traits: List<Trait>,
        val careers: List<Career>,
        val trappings: List<Trapping>,
        val diseases: List<Disease>,
        val journalEntries: List<JournalEntry>,
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
    TRAPPINGS,
    DISEASES,
    JOURNAL,
}
