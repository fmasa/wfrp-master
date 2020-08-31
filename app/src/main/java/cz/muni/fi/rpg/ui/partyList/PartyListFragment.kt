package cz.muni.fi.rpg.ui.partyList

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.joinParty.JoinPartyActivity
import cz.muni.fi.rpg.ui.partyList.adapter.PartyAdapter
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class PartyListFragment : BaseFragment(R.layout.fragment_party_list),
    AssemblePartyDialog.PartyCreationListener,
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val viewModel: PartyListViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partyListRecycler = view.findViewById<RecyclerView>(R.id.partyListRecycler)
        partyListRecycler.layoutManager = LinearLayoutManager(context)

        val userId = authViewModel.getUserId()

        val adapter = PartyAdapter(
            layoutInflater,
            userId,
            onClickListener = {
                if (it.gameMasterId == authViewModel.getUserId()) {
                    openGameMasterFragment(it.id)
                } else {
                    openCharacter(it.id, userId)
                }
            },
            onRemoveListener = {
                val message = getString(R.string.party_remove_confirmation)

                AlertDialog.Builder(requireContext())
                    .setPositiveButton(R.string.remove) { _, _ ->
                        launch {
                            viewModel.archive(it.id)
                            withContext(Dispatchers.Main) {
                                toast(R.string.message_party_removed, Toast.LENGTH_LONG)
                            }
                        }
                    }
                    .setNegativeButton(R.string.button_cancel, null)
                    .setMessage(getString(R.string.party_remove_confirmation))
                    .setMessage(
                        if (it.users.size > 1)
                            "$message\n\n${getString(R.string.party_remove_multiple_members)}"
                        else message
                    ).show()
            }
        )
        partyListRecycler.adapter = adapter

        viewModel.liveForUser(userId).observe(viewLifecycleOwner) {
            adapter.submitList(it)

            val noPartiesIcon = view.findViewById<View>(R.id.noPartiesIcon)
            val noPartiesText = view.findViewById<View>(R.id.noPartiesText)

            if (it.isNotEmpty()) {
                noPartiesIcon.visibility = View.GONE
                noPartiesText.visibility = View.GONE
                partyListRecycler.visibility = View.VISIBLE
            } else {
                noPartiesIcon.visibility = View.VISIBLE
                noPartiesText.visibility = View.VISIBLE
                partyListRecycler.visibility = View.GONE
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

    private fun openGameMasterFragment(partyId: UUID) = findNavController()
        .navigate(PartyListFragmentDirections.startGameMasterFragment(partyId))

    private fun openCharacter(partyId: UUID, userId: String) {
        findNavController().navigate(
            PartyListFragmentDirections.openCharacter(CharacterId.forUser(partyId, userId))
        )
    }
}