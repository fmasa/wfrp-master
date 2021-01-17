package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import java.util.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.platform.AmbientContext
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
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.common.composables.*
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing

@Composable
fun PartyListScreen(routing: Routing<Route.PartyList>) {

    val viewModel: PartyListViewModel by viewModel()
    var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }
    val context = AmbientContext.current

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
            FloatingActionsMenu(
                state = menuState,
                onToggleRequest = { menuState = it },
                iconRes = R.drawable.ic_add,
            ) {
                ExtendedFloatingActionButton(
                    icon = { Icon(vectorResource(R.drawable.ic_camera)) },
                    text = { Text(stringResource(R.string.scanCode_title)) },
                    onClick = {
                        routing.navigateTo(Route.InvitationScanner)
                        menuState = MenuState.COLLAPSED
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
                        createPartyDialogVisible = true
                        menuState = MenuState.COLLAPSED
                    }
                )
            }
        }
    ) {
        val userId = AmbientUser.current.id

        var removePartyDialogState by remember {
            mutableStateOf<DialogState<Party>>(DialogState.Closed())
        }

        removePartyDialogState.IfOpened { party ->
            RemovePartyDialog(
                party,
                viewModel,
                onDismissRequest = { removePartyDialogState = DialogState.Closed() },
            )
        }

        MainContainer(
            Modifier.clickable(
                onClick = { menuState = MenuState.COLLAPSED },
                indication = null,
            ),
            userId,
            viewModel,
            onClick = {
                if (it.gameMasterId == userId) {
                    routing.navigateTo(Route.GameMaster(it.id))
                } else {
                    routing.navigateTo(Route.CharacterDetail(CharacterId(it.id, userId)))
                }
            },
            onRemove = { removePartyDialogState = DialogState.Opened(it) },
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
    userId: String,
    viewModel: PartyListViewModel,
    onClick: (Party) -> Unit,
    onRemove: (Party) -> Unit,
) {
    val parties = remember { viewModel.liveForUser(userId) }.collectAsState(null)

    Box(modifier) {
        parties.value?.let {
            if (it.isEmpty()) {
                EmptyUI(
                    textId = R.string.no_parties_prompt,
                    subTextId = R.string.no_parties_sub_prompt,
                    drawableResourceId = R.drawable.ic_rally_the_troops,
                )
                return@let
            }

            PartyList(parties = it, onClick = onClick, onRemove = onRemove)
        }
    }
}

@Composable
fun PartyList(parties: List<Party>, onClick: (Party) -> Unit, onRemove: (Party) -> Unit) {
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

            ContextMenu(
                items = listOf(
                    ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove(party) })
                ),
                onDismissRequest = { contextMenuOpened.value = null },
                expanded = contextMenuOpened.value == party.id
            )
        }
    }
}