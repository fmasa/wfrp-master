package cz.frantisekmasa.wfrp_master.common.partyList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
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
import cz.frantisekmasa.wfrp_master.common.Plurals
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogAction
import cz.frantisekmasa.wfrp_master.common.changelog.ChangelogScreen
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.CharacterPickerScreen
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FloatingActionsMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.MenuState
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTipCard
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VISUAL_ONLY_ICON_DESCRIPTION
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreen
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationScannerScreen
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

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
            val navigation = LocalNavigationTransaction.current

            CreatePartyDialog(
                screenModel = viewModel,
                onSuccess = { partyId ->
                    createPartyDialogVisible = false
                    navigation.navigate(GameMasterScreen(partyId))
                },
                onDismissRequest = { createPartyDialogVisible = false },
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.parties_title_parties)) },
                    navigationIcon = { HamburgerButton() },
                    actions = {
                        val navigation = LocalNavigationTransaction.current

                        ChangelogAction(
                            rememberScreenModel(),
                            onClick = { navigation.navigate(ChangelogScreen) },
                        )
                    },
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
            },
        ) {
            var removePartyDialogState by remember {
                mutableStateOf<DialogState<PartyListItem>>(DialogState.Closed())
            }

            var leavePartyDialogState by remember {
                mutableStateOf<DialogState<PartyListItem>>(DialogState.Closed())
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

            val navigation = LocalNavigationTransaction.current

            MainContainer(
                Modifier,
                parties,
                onClick = {
                    navigation.navigate(
                        when {
                            it.isGameMaster -> GameMasterScreen(it.id)
                            it.singleCharacterId != null -> CharacterDetailScreen(it.singleCharacterId)
                            else -> CharacterPickerScreen(it.id)
                        },
                    )
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
    FloatingActionsMenu(
        state = state,
        onToggleRequest = { onStateChangeRequest(it) },
        icon = rememberVectorPainter(Icons.Rounded.Add),
    ) {
        if (LocalStaticConfiguration.current.platform == Platform.Android) {
            val navigation = LocalNavigationTransaction.current

            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Rounded.Camera, VISUAL_ONLY_ICON_DESCRIPTION) },
                text = { Text(stringResource(Str.parties_title_join_via_qr_code)) },
                onClick = {
                    navigation.navigate(InvitationScannerScreen())
                    onStateChangeRequest(MenuState.COLLAPSED)
                },
            )

            // TODO: Add alternative that allows entering URL directly
        }
        ExtendedFloatingActionButton(
            icon = {
                Icon(Icons.Rounded.GroupAdd, VISUAL_ONLY_ICON_DESCRIPTION)
            },
            text = { Text(stringResource(Str.parties_title_create_party)) },
            onClick = {
                onCreatePartyRequest()
                onStateChangeRequest(MenuState.COLLAPSED)
            },
        )
    }
}

@Composable
fun PartyItem(item: PartyListItem) {
    Column {
        val playersCount = item.playersCount

        ListItem(
            icon = { ItemIcon(Icons.Rounded.Group, ItemIcon.Size.Large) },
            text = { Text(item.name) },
            trailing =
                if (playersCount > 0) {
                    ({ Text(stringResource(Plurals.parties_player_count, playersCount, playersCount)) })
                } else {
                    null
                },
        )
        Divider()
    }
}

@Composable
private fun MainContainer(
    modifier: Modifier,
    parties: List<PartyListItem>?,
    onClick: (PartyListItem) -> Unit,
    onRemove: (PartyListItem) -> Unit,
    onLeaveRequest: (PartyListItem) -> Unit,
) {
    Box(modifier) {
        parties?.let {
            if (it.isEmpty()) {
                EmptyUI(
                    text = stringResource(Str.parties_messages_no_parties),
                    subText = stringResource(Str.parties_messages_no_parties_subtext),
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
    parties: List<PartyListItem>,
    onClick: (PartyListItem) -> Unit,
    onRemove: (PartyListItem) -> Unit,
    onLeaveRequest: (PartyListItem) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(bottom = Spacing.bottomPaddingUnderFab),
    ) {
        item {
            UserTipCard(
                UserTip.FEEDBACK_FORM,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }

        items(parties, key = { it.id }) { item ->
            WithContextMenu(
                onClick = { onClick(item) },
                items =
                    listOf(
                        if (item.isGameMaster) {
                            ContextMenu.Item(
                                stringResource(Str.common_ui_button_remove),
                                onClick = { onRemove(item) },
                            )
                        } else {
                            ContextMenu.Item(
                                stringResource(Str.parties_button_leave),
                                onClick = { onLeaveRequest(item) },
                            )
                        },
                    ),
            ) {
                PartyItem(item)
            }
        }
    }
}

@Parcelize
data class PartyListItem(
    val id: PartyId,
    val party: Party,
    val name: String,
    val isGameMaster: Boolean,
    val singleCharacterId: CharacterId?,
    val playersCount: Int,
) : Parcelable
