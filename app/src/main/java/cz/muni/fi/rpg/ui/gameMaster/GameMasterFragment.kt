package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.gameMaster.adapter.CharacterAdapter
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.android.synthetic.main.fragment_game_master.*
import org.koin.core.parameter.parametersOf
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class GameMasterFragment(
    private val jsonMapper: JsonMapper
) : BaseFragment(R.layout.fragment_game_master) {

    private val args: GameMasterFragmentArgs by navArgs()

    private val viewModel: GameMasterViewModel by viewModel { parametersOf(args.partyId) }

    private lateinit var invitation: Invitation

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        setViewVisibility(noCharactersIcon, isEmpty)
        setViewVisibility(noCharactersText, isEmpty)
        setViewVisibility(characterListRecycler, !isEmpty)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Created view for GameMasterFragment (partyId = ${args.partyId}")


        viewModel.party.right().observe(viewLifecycleOwner) { party ->
            setTitle(party.getName())
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

        inviteButton.setOnClickListener { showQrCode() }

        viewModel.getPlayers().observe(viewLifecycleOwner) { players ->
            if (players.isNotEmpty()) {
                val adapter = CharacterAdapter(layoutInflater)
                {
                    findNavController()
                        .navigate(
                            GameMasterFragmentDirections
                                .openCharacter((CharacterId(args.partyId, it.userId)))
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
        InvitationDialog(invitation, jsonMapper)
            .show(requireActivity().supportFragmentManager, InvitationDialog::class.simpleName)
    }
}