package cz.frantisekmasa.wfrp_master.common.compendium.journal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreenModel.JournalEntryItem
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreenModel.TreeItem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class JournalScreen(
    private val partyId: PartyId
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: JournalScreenModel = rememberScreenModel(arg = partyId)
        var newEntryDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current
        val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(null).value

        if (newEntryDialogOpened) {
            JournalEntryDialog(
                entry = null,
                onDismissRequest = { newEntryDialogOpened = false },
                onSaveRequest = {
                    screenModel.save(it)
                    navigation.navigate(JournalEntryScreen(partyId, it.id))
                }
            )
        }

        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

        SearchableList(
            data = screenModel.entries.collectWithLifecycle(null).value
                ?.let { SearchableList.Data.Loaded(it) } ?: SearchableList.Data.Loading,
            emptyUi = {
                EmptyUI(
                    text = stringResource(Str.journal_messages_no_entries),
                    subText = stringResource(Str.journal_messages_no_entries_subtext),
                    icon = Resources.Drawable.JournalEntry,
                )
            },
            title = stringResource(Str.compendium_title_journal),
            searchPlaceholder = "",
            searchableValue = { it.name },
            navigationIcon = { BackButton() },
            key = { it.id },
            itemContent = {
                JournalListItem(
                    entry = it,
                    coroutineScope = coroutineScope,
                    screenModel = screenModel,
                    onClick = { navigation.navigate(JournalEntryScreen(partyId, it.id)) },
                    isPinned = false,
                    isGameMaster = isGameMaster ?: false,
                )
            },
            defaultContent = {
                val tree = screenModel.entriesTree.collectWithLifecycle(null).value

                if (tree == null) {
                    FullScreenProgress()
                    return@SearchableList
                }

                LazyColumn {
                    items(tree) {
                        JournalTreeItem(
                            item = it,
                            coroutineScope = coroutineScope,
                            screenModel = screenModel,
                            onClick = { navigation.navigate(JournalEntryScreen(partyId, it.id)) },
                            isLast = false,
                            isGameMaster = isGameMaster ?: false,
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { newEntryDialogOpened = true }) {
                    Icon(
                        Icons.Rounded.Add,
                        stringResource(Str.compendium_icon_add_compendium_item),
                    )
                }
            }
        )
    }

    @Composable
    private fun JournalListItem(
        entry: JournalEntryItem,
        coroutineScope: CoroutineScope,
        screenModel: JournalScreenModel,
        onClick: () -> Unit,
        isPinned: Boolean,
        isGameMaster: Boolean,
    ) {
        WithContextMenu(
            items = listOf(
                ContextMenu.Item(
                    stringResource(Str.common_ui_button_duplicate),
                    onClick = {
                        coroutineScope.launch { screenModel.duplicate(entry.id) }
                    }
                ),
                ContextMenu.Item(
                    stringResource(Str.common_ui_button_remove),
                    onClick = { coroutineScope.launch { screenModel.remove(entry.id) } }
                ),
            ),
            onClick = { onClick() },
        ) {
            ListItem(
                icon = {
                    if (isPinned) {
                        ItemIcon(Icons.Rounded.PushPin)
                    } else {
                        ItemIcon(Resources.Drawable.JournalEntry)
                    }
                },
                text = { Text(entry.name) },
                trailing = {
                    if (isGameMaster) {
                        VisibilityIcon(entry.isVisibleToPlayers)
                    }
                },
            )
        }
    }

    @Composable
    private fun JournalTreeItem(
        item: TreeItem,
        coroutineScope: CoroutineScope,
        screenModel: JournalScreenModel,
        onClick: (JournalEntryItem) -> Unit,
        isLast: Boolean,
        isGameMaster: Boolean,
    ) {
        Column {

            when (item) {
                is TreeItem.Item -> {
                    JournalListItem(
                        entry = item.entry,
                        coroutineScope = coroutineScope,
                        screenModel = screenModel,
                        onClick = { onClick(item.entry) },
                        isPinned = item.isPinned,
                        isGameMaster = isGameMaster,
                    )
                }

                is TreeItem.Folder -> {
                    Column {
                        var expanded by rememberSaveable { mutableStateOf(false) }

                        ListItem(
                            icon = { ItemIcon(Icons.Rounded.Folder) },
                            text = { Text(item.name) },
                            trailing = { Text(item.count.toString()) },
                            modifier = Modifier.clickable { expanded = !expanded },
                        )

                        Column(
                            Modifier
                                .padding(start = Spacing.large)
                                .animateContentSize()
                        ) {
                            if (expanded) {
                                item.items.forEachIndexed { index, subItem ->
                                    key(subItem.key) {
                                        JournalTreeItem(
                                            item = subItem,
                                            coroutineScope = coroutineScope,
                                            screenModel = screenModel,
                                            onClick = onClick,
                                            isLast = index == item.items.lastIndex,
                                            isGameMaster = isGameMaster,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!isLast) {
                Divider()
            }
        }
    }
}
