package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.gameMaster.adapter.CharacterAdapter
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.android.synthetic.main.fragment_party_summary.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class PartySummaryFragment : Fragment(R.layout.fragment_party_summary) {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = PartySummaryFragment().apply {
            arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
        }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: GameMasterViewModel by viewModel { parametersOf(partyId) }

    private lateinit var invitation: Invitation

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        noCharactersIcon.toggleVisibility(isEmpty)
        noCharactersText.toggleVisibility(isEmpty)
        characterListRecycler.toggleVisibility(!isEmpty)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.party.right().observe(viewLifecycleOwner) { party ->
            invitation = party.getInvitation()

            inviteButton.isEnabled = true

            ambitionsCard.setValue(party.getAmbitions())
            ambitionsCard.setOnClickListener {
                ChangeAmbitionsDialog
                    .newInstance(getString(R.string.title_party_ambitions), party.getAmbitions())
                    .setOnSaveListener {
                        viewModel.updatePartyAmbitions(it)
                    }.show(childFragmentManager, "ChangeAmbitionsDialog")
            }
        }

        createCharacterButton.setOnClickListener {
            findNavController().navigate(
                GameMasterFragmentDirections.createCharacter(partyId, null)
            )
        }

        inviteButton.setOnClickListener { showQrCode() }

        viewModel.getPlayers().observe(viewLifecycleOwner) { players ->
            val directions = GameMasterFragmentDirections
            if (players.isNotEmpty()) {
                val adapter = CharacterAdapter(layoutInflater)
                {
                    findNavController().navigate(
                        when (it) {
                            is Player.UserWithoutCharacter -> directions.createCharacter(
                                partyId,
                                it.userId
                            )
                            is Player.ExistingCharacter -> directions.openCharacter(
                                CharacterId(partyId, it.character.id)
                            )
                        }
                    )
                }
                characterListRecycler.adapter = adapter
                characterListRecycler.layoutManager = LinearLayoutManager(context)

                adapter.submitList(players)

                setEmptyCollectionView(false)
            } else {
                setEmptyCollectionView(true)
            }
        }
    }

    private fun showQrCode() {
        InvitationDialog.newInstance(invitation)
            .show(requireActivity().supportFragmentManager, null)
    }
}