package cz.muni.fi.rpg.ui.partyList

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import com.getbase.floatingactionbutton.FloatingActionsMenu
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.joinParty.JoinPartyActivity
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.ui.common.composables.*

class PartyListFragment : BaseFragment(R.layout.fragment_party_list),
    AssemblePartyDialog.PartyCreationListener,
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val viewModel: PartyListViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = authViewModel.getUserId()

        view.findViewById<ComposeView>(R.id.compose).setContent {
            Theme {
                MainContainer(
                    userId,
                    viewModel,
                    onClick = {
                        if (it.gameMasterId == authViewModel.getUserId()) {
                            openGameMasterFragment(it.id)
                        } else {
                            openCharacter(it.id, userId)
                        }
                    },
                    onRemove = ::removeParty,
                )
            }
        }

        val fabMenu = view.findViewById<FloatingActionsMenu>(R.id.fabMenu)
        view.findViewById<View>(R.id.assembleNewParty).setOnClickListener {
            AssemblePartyDialog().show(childFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }

        view.findViewById<View>(R.id.scanQrCode).setOnClickListener {
            JoinPartyActivity.start(requireContext())
            fabMenu.collapse()
        }
    }

    override fun onSuccessfulCreation(party: Party) {
        if (party.isSinglePlayer())
            openCharacter(party.id, authViewModel.getUserId())
        else openGameMasterFragment(party.id)
    }

    private fun removeParty(party: Party) {
        val message = getString(R.string.party_remove_confirmation)

        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.remove) { _, _ ->
                launch {
                    viewModel.archive(party.id)
                    withContext(Dispatchers.Main) {
                        toast(R.string.message_party_removed, Toast.LENGTH_LONG)
                    }
                }
            }
            .setNegativeButton(R.string.button_cancel, null)
            .setMessage(getString(R.string.party_remove_confirmation))
            .setMessage(
                if (party.users.size > 1)
                    "$message\n\n${getString(R.string.party_remove_multiple_members)}"
                else message
            ).show()
    }

    private fun openGameMasterFragment(partyId: UUID) = findNavController()
        .navigate(PartyListFragmentDirections.startGameMasterFragment(partyId))

    private fun openCharacter(partyId: UUID, userId: String) {
        findNavController().navigate(
            PartyListFragmentDirections.openCharacter(CharacterId.forUser(partyId, userId))
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
    userId: String,
    viewModel: PartyListViewModel,
    onClick: (Party) -> Unit,
    onRemove: (Party) -> Unit,
) {
    val parties = viewModel.liveForUser(userId).observeAsState()

    parties.value?.let {
        if (it.isEmpty()) {
            EmptyUI(R.string.no_parties_prompt, R.drawable.ic_rally_the_troops)
            return
        }

        PartyList(parties = it, onClick = onClick, onRemove = onRemove)
    }
}

@Composable
fun PartyList(parties: List<Party>, onClick: (Party) -> Unit, onRemove: (Party) -> Unit) {
    ScrollableColumn(Modifier.padding(top = 12.dp)) {
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