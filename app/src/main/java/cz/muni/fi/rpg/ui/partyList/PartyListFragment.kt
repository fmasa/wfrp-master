package cz.muni.fi.rpg.ui.partyList

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.joinParty.JoinPartyActivity
import cz.muni.fi.rpg.ui.partyList.adapter.PartyAdapter
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.android.synthetic.main.fragment_party_list.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class PartyListFragment(
    private val parties: PartyRepository
) : BaseFragment(R.layout.fragment_party_list) {

    private val authViewModel: AuthenticationViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partyListRecycler.layoutManager = LinearLayoutManager(context)

        val userId = authViewModel.getUserId()

        val adapter = PartyAdapter(layoutInflater) {
            if (it.gameMasterId == authViewModel.getUserId()) {
                openGameMasterFragment(it.id)
            } else {
                findNavController().navigate(
                    PartyListFragmentDirections
                        .openCharacter(CharacterId(partyId = it.id, userId = userId))
                )
            }
        }
        partyListRecycler.adapter = adapter

        parties.forUser(userId).observe(viewLifecycleOwner) {
            adapter.submitList(it)

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

        assembleNewParty.setOnClickListener {
            AssemblePartyDialog(
                userId,
                { party -> openGameMasterFragment(party.id) },
                parties
            ).show(childFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }

        scanQrCode.setOnClickListener {
            JoinPartyActivity.start(requireContext())
            fabMenu.collapse()
        }
    }

    private fun openGameMasterFragment(partyId: UUID) = findNavController()
        .navigate(PartyListFragmentDirections.startGameMasterFragment(partyId))
}