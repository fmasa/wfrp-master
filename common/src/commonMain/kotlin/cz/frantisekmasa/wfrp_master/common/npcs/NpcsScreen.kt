package cz.frantisekmasa.wfrp_master.common.npcs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NpcsScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val navigation = LocalNavigationTransaction.current
        var processing by remember { mutableStateOf(false) }

        val screenModel: NpcsScreenModel = rememberScreenModel(arg = partyId)
        val npcs by screenModel.npcs.collectWithLifecycle(null)
        val (npcToRemove, setNpcToRemove) = remember { mutableStateOf<Character?>(null) }

        val data by derivedStateOf {
            if (processing) {
                return@derivedStateOf SearchableList.Data.Loading
            }

            npcs?.let { SearchableList.Data.Loaded(it) }
                ?: SearchableList.Data.Loading
        }

        val coroutineScope = rememberCoroutineScope()

        if (npcToRemove != null) {
            AlertDialog(
                onDismissRequest = { setNpcToRemove(null) },
                text = { Text(stringResource(Str.npcs_messages_removal_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                processing = true
                                setNpcToRemove(null)
                                screenModel.archiveNpc(npcToRemove)
                                processing = false
                            }
                        }
                    ) {
                        Text(stringResource(Str.common_ui_button_remove).uppercase())
                    }
                },
                dismissButton = {
                    TextButton(onClick = { setNpcToRemove(null) }) {
                        Text(stringResource(Str.common_ui_button_cancel).uppercase())
                    }
                }
            )
        }

        SearchableList(
            data = data,
            navigationIcon = { HamburgerButton() },
            title = stringResource(Str.npcs_title_plural),
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigation.navigate(
                            CharacterCreationScreen(partyId, CharacterType.NPC, null)
                        )
                    }
                ) {
                    Icon(Icons.Rounded.Add, stringResource(Str.npcs_button_add_npc))
                }
            }
        ) { npc ->
            Column {
                val unknownErrorMessage = stringResource(Str.messages_error_unknown)
                val snackbarHolder = LocalPersistentSnackbarHolder.current

                WithContextMenu(
                    onClick = {
                        navigation.navigate(CharacterDetailScreen(CharacterId(partyId, npc.id)))
                    },
                    items = listOf(
                        ContextMenu.Item(stringResource(Str.common_ui_button_duplicate)) {
                            coroutineScope.launch(Dispatchers.IO) {
                                processing = true
                                try {
                                    screenModel.duplicate(npc)
                                } catch (e: Exception) {
                                    Napier.e("Failed to duplicate NPC", e)
                                    snackbarHolder.showSnackbar(unknownErrorMessage)
                                } finally {
                                    processing = false
                                }
                            }
                        },
                        ContextMenu.Item(stringResource(Str.common_ui_button_remove)) {
                            setNpcToRemove(npc)
                        }
                    )
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
}
