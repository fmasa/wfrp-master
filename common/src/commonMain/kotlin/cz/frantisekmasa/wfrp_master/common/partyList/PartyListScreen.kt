package cz.frantisekmasa.wfrp_master.common.partyList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogAction
import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogScreen
import cz.frantisekmasa.wfrp_master.common.character.CharacterPickerScreen
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FloatingActionsMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.MenuState
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationScannerScreen
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

object PartyListScreen : Screen {
    override val key = "parties"

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel<PartyListScreenModel>()
        val userId = LocalUser.current.id
        val parties = remember { viewModel.liveForUser(userId) }.collectWithLifecycle(null).value

        var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }
        var createPartyDialogVisible by rememberSaveable { mutableStateOf(false) }

        if (createPartyDialogVisible) {
            val navigator = LocalNavigator.currentOrThrow

            CreatePartyDialog(
                screenModel = viewModel,
                onSuccess = { partyId ->
                    createPartyDialogVisible = false
                    navigator.push(GameMasterScreen(partyId))
                },
                onDismissRequest = { createPartyDialogVisible = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(LocalStrings.current.parties.titleParties) },
                    navigationIcon = { HamburgerButton() },
                    actions = {
                        val navigator = LocalNavigator.currentOrThrow

                        ChangelogAction(
                            rememberScreenModel(),
                            onClick = { navigator.push(ChangelogScreen) },
                        )
                    }
                )
            },
            modifier = Modifier.fillMaxHeight(),
            floatingActionButton = {
                if (parties == null) {
                    return@Scaffold
                }

                Menu(
                    state = menuState,
                    onStateChangeRequest = { menuState = it },
                    onCreatePartyRequest = { createPartyDialogVisible = true },
                )
            }
        ) {
            var removePartyDialogState by remember {
                mutableStateOf<DialogState<Party>>(DialogState.Closed())
            }

            var leavePartyDialogState by remember {
                mutableStateOf<DialogState<Party>>(DialogState.Closed())
            }

            removePartyDialogState.IfOpened { party ->
                RemovePartyDialog(
                    party,
                    viewModel,
                    onDismissRequest = { removePartyDialogState = DialogState.Closed() },
                )
            }

            leavePartyDialogState.IfOpened { party ->
                LeavePartyDialog(
                    party,
                    viewModel,
                    onDismissRequest = { leavePartyDialogState = DialogState.Closed() },
                )
            }

            val navigator = LocalNavigator.currentOrThrow

            MainContainer(
                Modifier,
                parties,
                onClick = {
                    if (it.gameMasterId == userId) {
                        navigator.push(GameMasterScreen(it.id))
                    } else {
                        navigator.push(CharacterPickerScreen(it.id))
                    }
                },
                onRemove = { removePartyDialogState = DialogState.Opened(it) },
                onLeaveRequest = { leavePartyDialogState = DialogState.Opened(it) },
            )
        }
    }
}

@Composable
private fun Menu(
    state: MenuState,
    onStateChangeRequest: (MenuState) -> Unit,
    onCreatePartyRequest: () -> Unit,
) {
    val strings = LocalStrings.current

    FloatingActionsMenu(
        state = state,
        onToggleRequest = { onStateChangeRequest(it) },
        icon = rememberVectorPainter(Icons.Rounded.Add),
    ) {
        if (LocalStaticConfiguration.current.platform == Platform.Android) {
            val navigator = LocalNavigator.currentOrThrow

            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Rounded.Camera, VisualOnlyIconDescription) },
                text = { Text(strings.parties.titleJoinViaQrCode) },
                onClick = {
                    navigator.push(InvitationScannerScreen())
                    onStateChangeRequest(MenuState.COLLAPSED)
                }
            )

            // TODO: Add alternative that allows entering URL directly
        }
        ExtendedFloatingActionButton(
            icon = {
                Icon(Icons.Rounded.GroupAdd, VisualOnlyIconDescription)
            },
            text = { Text(strings.parties.titleCreateParty) },
            onClick = {
                onCreatePartyRequest()
                onStateChangeRequest(MenuState.COLLAPSED)
            }
        )
    }
}

@Composable
fun PartyItem(party: Party) {
    Column {
        val playersCount = party.playersCount

        ListItem(
            icon = { ItemIcon(Icons.Rounded.Group, ItemIcon.Size.Large) },
            text = { Text(party.name) },
            trailing = if (playersCount > 0)
                ({ Text(LocalStrings.current.parties.numberOfPlayers(playersCount)) })
            else null,
        )
        Divider()
    }
}

@Composable
private fun MainContainer(
    modifier: Modifier,
    parties: List<Party>?,
    onClick: (Party) -> Unit,
    onRemove: (Party) -> Unit,
    onLeaveRequest: (Party) -> Unit,
) {
    Box(modifier) {
        parties?.let {
            if (it.isEmpty()) {
                val messages = LocalStrings.current.parties.messages

                EmptyUI(
                    text = messages.noParties,
                    subText = messages.noPartiesSubtext,
                    icon = Resources.Drawable.PartyNotFound,
                )
                return@let
            }

            PartyList(
                parties = it,
                onClick = onClick,
                onRemove = onRemove,
                onLeaveRequest = onLeaveRequest,
            )
        }
    }
}

@Composable
fun PartyList(
    parties: List<Party>,
    onClick: (Party) -> Unit,
    onRemove: (Party) -> Unit,
    onLeaveRequest: (Party) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(top = Spacing.medium, bottom = Spacing.bottomPaddingUnderFab),
    ) {
        items(parties, key = { it.id }) { party ->
            val isGameMaster =
                LocalUser.current.id == party.gameMasterId || party.gameMasterId == null
            val strings = LocalStrings.current

            WithContextMenu(
                onClick = { onClick(party) },
                items = listOf(
                    if (isGameMaster)
                        ContextMenu.Item(
                            strings.commonUi.buttonRemove,
                            onClick = { onRemove(party) },
                        )
                    else ContextMenu.Item(
                        strings.parties.buttonLeave,
                        onClick = { onLeaveRequest(party) },
                    )
                )
            ) {
                PartyItem(party)
            }
        }
    }
}
