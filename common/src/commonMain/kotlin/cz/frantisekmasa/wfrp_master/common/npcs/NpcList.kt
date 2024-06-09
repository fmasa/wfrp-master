package cz.frantisekmasa.wfrp_master.common.npcs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NpcList(
    title: String,
    navigationIcon: @Composable () -> Unit,
    screenModel: NpcsScreenModel,
    floatingActionButton: @Composable () -> Unit,
    onClick: (CharacterId) -> Unit,
    data: SearchableList.Data<NpcList.Item>,
) {
    var processing by remember { mutableStateOf(false) }

    val (npcIdToRemove, setNpcIdToRemove) = remember { mutableStateOf<LocalCharacterId?>(null) }

    val coroutineScope = rememberCoroutineScope()

    if (npcIdToRemove != null) {
        AlertDialog(
            onDismissRequest = { setNpcIdToRemove(null) },
            text = { Text(stringResource(Str.npcs_messages_removal_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            processing = true
                            setNpcIdToRemove(null)
                            screenModel.archiveNpc(npcIdToRemove)
                            processing = false
                        }
                    },
                ) {
                    Text(stringResource(Str.common_ui_button_remove).uppercase())
                }
            },
            dismissButton = {
                TextButton(onClick = { setNpcIdToRemove(null) }) {
                    Text(stringResource(Str.common_ui_button_cancel).uppercase())
                }
            },
        )
    }

    val derivedData by derivedStateOf {
        if (processing) {
            return@derivedStateOf SearchableList.Data.Loading
        }

        data
    }

    SearchableList(
        data = derivedData,
        navigationIcon = navigationIcon,
        title = title,
        searchPlaceholder = stringResource(Str.npcs_search_placeholder),
        emptyUi = {
            EmptyUI(
                text = stringResource(Str.npcs_messages_no_npcs),
                subText = stringResource(Str.npcs_messages_no_npcs_subtext),
                icon = Resources.Drawable.Npc,
            )
        },
        key = { it.id },
        searchableValue = { it.name },
        floatingActionButton = floatingActionButton,
    ) { npc ->
        Column {
            val unknownErrorMessage = stringResource(Str.messages_error_unknown)
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            WithContextMenu(
                onClick = { onClick(CharacterId(screenModel.partyId, npc.id)) },
                items =
                    listOf(
                        ContextMenu.Item(stringResource(Str.common_ui_button_duplicate)) {
                            coroutineScope.launch(Dispatchers.IO) {
                                processing = true
                                try {
                                    screenModel.duplicate(
                                        npcId = npc.id,
                                        originalName = npc.name,
                                    )
                                } catch (e: Exception) {
                                    Napier.e("Failed to duplicate NPC", e)
                                    snackbarHolder.showSnackbar(unknownErrorMessage)
                                } finally {
                                    processing = false
                                }
                            }
                        },
                        ContextMenu.Item(stringResource(Str.common_ui_button_remove)) {
                            setNpcIdToRemove(npc.id)
                        },
                    ),
            ) {
                ListItem(
                    icon = {
                        CharacterAvatar(
                            npc.avatarUrl,
                            ItemIcon.Size.Small,
                            fallback = Resources.Drawable.Npc,
                        )
                    },
                    text = { Text(npc.name) },
                )
            }

            Divider()
        }
    }
}

object NpcList {
    data class Item(
        val id: LocalCharacterId,
        val name: String,
        val avatarUrl: String?,
    )
}
