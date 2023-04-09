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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NpcsScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var processing by remember { mutableStateOf(false) }

        val strings = LocalStrings.current
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
                text = { Text(LocalStrings.current.npcs.messages.removalConfirmation) },
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
                        Text(LocalStrings.current.commonUi.buttonRemove)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { setNpcToRemove(null) }) {
                        Text(LocalStrings.current.commonUi.buttonCancel)
                    }
                }
            )
        }

        SearchableList(
            data = data,
            navigationIcon = { HamburgerButton() },
            title = strings.npcs.titlePlural,
            searchPlaceholder = strings.npcs.searchPlaceholder,
            emptyUi = {
                EmptyUI(
                    text = strings.npcs.messages.noNpcs,
                    subText = strings.npcs.messages.noNpcsSubtext,
                    icon = Resources.Drawable.Npc,
                )
            },
            key = { it.id },
            searchableValue = { it.name },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(
                            CharacterCreationScreen(partyId, CharacterType.NPC, null)
                        )
                    }
                ) {
                    Icon(Icons.Rounded.Add, LocalStrings.current.npcs.buttonAddNpc)
                }
            }
        ) { npc ->
            Column {
                WithContextMenu(
                    onClick = {
                        navigator.push(CharacterDetailScreen(CharacterId(partyId, npc.id)))
                    },
                    items = listOf(
                        ContextMenu.Item(LocalStrings.current.commonUi.buttonDuplicate) {
                            coroutineScope.launch(Dispatchers.IO) {
                                processing = true
                                try {
                                    screenModel.duplicate(npc)
                                } finally {
                                    processing = false
                                }
                            }
                        },
                        ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove) {
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
