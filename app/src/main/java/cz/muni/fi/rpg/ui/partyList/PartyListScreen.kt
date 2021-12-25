package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
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
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.BuyPremiumPrompt
import cz.muni.fi.rpg.ui.common.composables.FloatingActionsMenu
import cz.muni.fi.rpg.ui.common.composables.MenuState
import cz.muni.fi.rpg.viewModels.PartyListViewModel

@Composable
fun PartyListScreen(routing: Routing<Route.PartyList>) {
    val viewModel: PartyListViewModel by viewModel()
    val userId = LocalUser.current.id
    val parties = remember { viewModel.liveForUser(userId) }.collectWithLifecycle(null).value

    var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }
    var createPartyDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (createPartyDialogVisible) {
        CreatePartyDialog(
            viewModel = viewModel,
            onSuccess = { partyId ->
                createPartyDialogVisible = false
                routing.navigateTo(Route.GameMaster(partyId))
            },
            onDismissRequest = { createPartyDialogVisible = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parties") },
                navigationIcon = { HamburgerButton() }
            )
        },
        modifier = Modifier.fillMaxHeight(),
        floatingActionButton = {
            if (parties == null) {
                return@Scaffold
            }

            Menu(
                state = menuState,
                routing = routing,
                onStateChangeRequest = { menuState = it },
                onCreatePartyRequest = { createPartyDialogVisible = true },
                partyCount = parties.size
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

        MainContainer(
            Modifier,
            parties,
            onClick = {
                if (it.gameMasterId == userId) {
                    routing.navigateTo(Route.GameMaster(it.id))
                } else {
                    routing.navigateTo(Route.CharacterDetail(CharacterId(it.id, userId)))
                }
            },
            onRemove = { removePartyDialogState = DialogState.Opened(it) },
            onLeaveRequest = { leavePartyDialogState = DialogState.Opened(it) },
        )
    }
}

@Composable
private fun Menu(
    state: MenuState,
    routing: Routing<Route.PartyList>,
    onStateChangeRequest: (MenuState) -> Unit,
    onCreatePartyRequest: () -> Unit,
    partyCount: Int
) {
    val premiumViewModel = providePremiumViewModel()

    if (partyCount >= PremiumViewModel.FREE_PARTY_COUNT && premiumViewModel.active != true) {
        var premiumPromptVisible by remember { mutableStateOf(false) }

        FloatingActionButton(onClick = { premiumPromptVisible = true }) {
            Icon(Icons.Rounded.Redeem, stringResource(R.string.buy_premium))
        }

        if (premiumPromptVisible) {
            BuyPremiumPrompt(onDismissRequest = { premiumPromptVisible = false })
        }

        return
    }

    FloatingActionsMenu(
        state = state,
        onToggleRequest = { onStateChangeRequest(it) },
        icon = rememberVectorPainter(Icons.Rounded.Add),
    ) {
        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Rounded.Camera, VisualOnlyIconDescription) },
            text = { Text(stringResource(R.string.scanCode_title)) },
            onClick = {
                routing.navigateTo(Route.InvitationScanner)
                onStateChangeRequest(MenuState.COLLAPSED)
            }
        )
        ExtendedFloatingActionButton(
            icon = {
                Icon(Icons.Rounded.GroupAdd, VisualOnlyIconDescription)
            },
            text = { Text(stringResource(R.string.assembleParty_title)) },
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
        val playersCount = party.getPlayerCounts()

        ListItem(
            icon = { ItemIcon(Icons.Rounded.Group, ItemIcon.Size.Large) },
            text = { Text(party.getName()) },
            trailing = if (playersCount > 0)
                ({ Text(stringResource(R.string.players_number, playersCount)) })
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
                EmptyUI(
                    textId = R.string.no_parties_prompt,
                    subTextId = R.string.no_parties_sub_prompt,
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
        contentPadding = PaddingValues(top = 12.dp, bottom = Spacing.bottomPaddingUnderFab)
    ) {
        items(parties) { party ->
            val isGameMaster =
                LocalUser.current.id == party.gameMasterId || party.gameMasterId == null

            WithContextMenu(
                onClick = { onClick(party) },
                items = listOf(
                    if (isGameMaster)
                        ContextMenu.Item(
                            stringResource(R.string.remove),
                            onClick = { onRemove(party) },
                        )
                    else ContextMenu.Item(
                        stringResource(R.string.button_leave),
                        onClick = { onLeaveRequest(party) },
                    )
                )
            ) {
                PartyItem(party)
            }
        }
    }
}
