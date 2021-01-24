package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import java.util.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.common.composables.*
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.ui.common.BuyPremiumPrompt

@Composable
fun PartyListScreen(routing: Routing<Route.PartyList>) {
    val viewModel: PartyListViewModel by viewModel()
    val userId = AmbientUser.current.id
    val parties = remember { viewModel.liveForUser(userId) }.observeAsState().value

    var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }
    var createPartyDialogVisible by savedInstanceState { false }

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
            Modifier.clickable(
                onClick = { menuState = MenuState.COLLAPSED },
                indication = null,
            ),
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
            Icon(vectorResource(R.drawable.ic_premium))
        }

        if (premiumPromptVisible) {
            BuyPremiumPrompt(onDismissRequest = { premiumPromptVisible = false })
        }

        return
    }

    FloatingActionsMenu(
        state = state,
        onToggleRequest = { onStateChangeRequest(it) },
        iconRes = R.drawable.ic_add,
    ) {
        ExtendedFloatingActionButton(
            icon = { Icon(vectorResource(R.drawable.ic_camera)) },
            text = { Text(stringResource(R.string.scanCode_title)) },
            onClick = {
                routing.navigateTo(Route.InvitationScanner)
                onStateChangeRequest(MenuState.COLLAPSED)
            }
        )
        ExtendedFloatingActionButton(
            icon = {
                loadVectorResource(R.drawable.ic_group_add).resource.resource?.let {
                    Icon(it)
                }
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
fun PartyItem(party: Party, onClick: () -> Unit, onLongPress: () -> Unit) {
    val playersCount = party.getPlayerCounts()

    ListItem(
        icon = { ItemIcon(R.drawable.ic_group, ItemIcon.Size.Large) },
        text = { Text(party.getName()) },
        trailing = if (playersCount > 0)
            ({ Text(stringResource(R.string.players_number, playersCount)) })
        else null,
        modifier = Modifier
            .clickable(onClick = onClick)
            .longPressGestureFilter { onLongPress() },
    )
    Divider()
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
                    drawableResourceId = R.drawable.ic_rally_the_troops,
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
    ScrollableColumn(
        Modifier
            .padding(top = 12.dp)
            .fillMaxHeight()
    ) {
        val contextMenuOpened = remember { mutableStateOf<PartyId?>(null) }

        for (party in parties) {
            PartyItem(party,
                onClick = { onClick(party) },
                onLongPress = { contextMenuOpened.value = party.id }
            )

            val isGameMaster =
                AmbientUser.current.id == party.gameMasterId || party.gameMasterId == null

            ContextMenu(
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
                ),
                onDismissRequest = { contextMenuOpened.value = null },
                expanded = contextMenuOpened.value == party.id
            )
        }
    }
}