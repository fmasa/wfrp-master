package cz.muni.fi.rpg.ui.partyList

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import cz.frantisekmasa.wfrp_master.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.fragmentManager
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.common.composables.*
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing

@Composable
fun PartyListScreen(routing: Routing<Route.PartyList>) {
    val menuState = remember { mutableStateOf(MenuState.COLLAPSED) }
    val fragmentManager = fragmentManager()
    val context = AmbientContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parties") },
                navigationIcon = { HamburgerButton() }
            )
        },
        modifier = Modifier.fillMaxHeight(),
        floatingActionButton = {
            FloatingActionsMenu(menuState) {
                ExtendedFloatingActionButton(
                    icon = { Icon(vectorResource(R.drawable.ic_camera)) },
                    text = { Text(stringResource(R.string.scanCode_title)) },
                    onClick = {
                        routing.backStack.push(Route.InvitationScanner)
                        menuState.value = MenuState.COLLAPSED
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
                        AssemblePartyDialog()
                            .setOnSuccessListener { party ->
                                routing.backStack.push(Route.GameMaster(party.id))
                            }
                            .show(fragmentManager, null)
                        menuState.value = MenuState.COLLAPSED
                    }
                )
            }
        }
    ) {
        val viewModel: PartyListViewModel by viewModel()
        val coroutineScope = rememberCoroutineScope()
        val userId = AmbientUser.current.id

        MainContainer(
            Modifier.clickable(
                onClick = { menuState.value = MenuState.COLLAPSED },
                indication = null,
            ),
            userId,
            viewModel,
            onClick = {
                if (it.gameMasterId == userId) {
                    routing.backStack.push(Route.GameMaster(it.id))
                } else {
                    routing.backStack.push(Route.CharacterDetail(CharacterId(it.id, userId)))
                }
            },
            onRemove = { with(coroutineScope) { removeParty(context, viewModel, it) } },
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
    val parties = viewModel.liveForUser(userId).collectAsState(null)

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
    ScrollableColumn(Modifier.padding(top = 12.dp).fillMaxHeight()) {
        val contextMenuOpened = remember { mutableStateOf<UUID?>(null) }

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

private fun CoroutineScope.removeParty(
    context: Context,
    viewModel: PartyListViewModel,
    party: Party
) {
    val message = context.getString(R.string.party_remove_confirmation)

    AlertDialog.Builder(context)
        .setPositiveButton(R.string.remove) { _, _ ->
            launch(Dispatchers.IO) {
                viewModel.archive(party.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.message_party_removed, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        .setNegativeButton(R.string.button_cancel, null)
        .setMessage(context.getString(R.string.party_remove_confirmation))
        .setMessage(
            if (party.users.size > 1)
                "$message\n\n${context.getString(R.string.party_remove_multiple_members)}"
            else message
        ).show()
}
