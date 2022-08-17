package cz.frantisekmasa.wfrp_master.common.npcs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NpcsScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val focusRequester = remember { FocusRequester() }
        var searchedValue by rememberSaveable { mutableStateOf("") }
        var searchActive by rememberSaveable { mutableStateOf(false) }

        val screenModel: NpcsScreenModel = rememberScreenModel(arg = partyId)
        val npcs = screenModel.npcs.collectWithLifecycle(null).value

        if (npcs == null) {
            FullScreenProgress()
            return
        }

        Scaffold(
            topBar = {
                val searchVisible by derivedStateOf { searchActive || searchedValue != "" }
                val strings = LocalStrings.current

                TopAppBar(
                    navigationIcon = { HamburgerButton() },
                    title = {
                        if (searchVisible) {
                            ProvideTextStyle(MaterialTheme.typography.body1) {
                                TextField(
                                    colors = textFieldColors(),
                                    value = searchedValue,
                                    onValueChange = { searchedValue = it },
                                    singleLine = true,
                                    placeholder = { Text(strings.npcs.searchPlaceholder) },
                                    modifier = Modifier.focusRequester(focusRequester),
                                )
                            }
                        } else {
                            Text(strings.npcs.titlePlural)
                        }

                        DisposableEffect(searchVisible) {
                            if (searchVisible) {
                                focusRequester.requestFocus()
                            }

                            onDispose { }
                        }
                    },
                    actions = {
                        if (searchActive) {
                            IconAction(
                                Icons.Rounded.Close,
                                strings.commonUi.buttonDismiss,
                                onClick = {
                                    searchedValue = ""
                                    searchActive = false
                                }
                            )
                        } else {
                            IconAction(
                                Icons.Rounded.Search,
                                strings.commonUi.search,
                                onClick = {
                                    searchActive = true
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(CharacterCreationScreen(partyId, CharacterType.NPC, null))
                    }
                ) {
                    Icon(Icons.Rounded.Add, LocalStrings.current.npcs.buttonAddNpc)
                }
            }
        ) {
            val messages = LocalStrings.current.npcs.messages

            if (npcs.isEmpty()) {
                EmptyUI(
                    text = messages.noNpcs,
                    subText = messages.noNpcsSubtext,
                    icon = Resources.Drawable.Npc,
                )
                return@Scaffold
            }

            val filteredNpcs by derivedStateOf {
                if (searchedValue == "")
                    npcs
                else npcs.filter { it.name.contains(searchedValue, ignoreCase = true) }
            }

            if (filteredNpcs.isEmpty()) {
                EmptyUI(
                    text = messages.noNpcsSearched,
                    subText = messages.noNpcsSearchedSubtext,
                    icon = Icons.Rounded.SearchOff,
                )
                return@Scaffold
            }

            val coroutineScope = rememberCoroutineScope()
            var removing by remember { mutableStateOf(false) }

            if (removing) {
                FullScreenProgress()
                return@Scaffold
            }

            NpcList(
                npcs = filteredNpcs,
                onClick = { navigator.push(CharacterDetailScreen(CharacterId(partyId, it.id))) },
                onRemoveRequest = {
                    coroutineScope.launch(Dispatchers.IO) {
                        removing = true
                        screenModel.archiveNpc(it)
                        removing = false
                    }
                }
            )
        }
    }

    @Stable
    @Composable
    private fun textFieldColors(): TextFieldColors {
        return TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = MaterialTheme.colors.onPrimary,
            placeholderColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    }

    @Composable
    private fun NpcList(
        npcs: List<Character>,
        onRemoveRequest: (Character) -> Unit,
        onClick: (Character) -> Unit,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = Spacing.medium,
                bottom = Spacing.bottomPaddingUnderFab,
            ),
        ) {
            items(npcs, key = { it.id }) { npc ->
                Column {
                    WithContextMenu(
                        onClick = { onClick(npc) },
                        items = listOf(
                            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove) {
                                onRemoveRequest(npc)
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
}