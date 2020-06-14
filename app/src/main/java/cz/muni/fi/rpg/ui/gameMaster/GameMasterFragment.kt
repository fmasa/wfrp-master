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
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.gameMaster.adapter.CharacterAdapter
import cz.muni.fi.rpg.viewModels.PartyViewModel
import kotlinx.android.synthetic.main.fragment_game_master.*
import org.koin.core.parameter.parametersOf
import org.koin.android.viewmodel.ext.android.viewModel

class GameMasterFragment(
    private val jsonMapper: JsonMapper,
    private val characterRepository: CharacterRepository

) : BaseFragment(R.layout.fragment_game_master) {

    private val args: GameMasterFragmentArgs by navArgs()

    private val partyViewModel: PartyViewModel by viewModel { parametersOf(args.partyId) }

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


        partyViewModel.party.right().observe(viewLifecycleOwner) { party ->
            setTitle(party.name)
            invitation = party.getInvitation()

            inviteButton.isEnabled = true

            ambitionsCard.setValue(party.getAmbitions())
            ambitionsCard.setOnClickListener {
                ChangeAmbitionsDialog
                    .newInstance(getString(R.string.title_party_ambitions), party.getAmbitions())
                    .setOnSaveListener {
                        partyViewModel.updatePartyAmbitions(it)
                    }.show(childFragmentManager, "ChangeAmbitionsDialog")
            }
        }

        inviteButton.setOnClickListener { showQrCode() }

        characterRepository.inParty(args.partyId).observe(viewLifecycleOwner) { characters ->
            if (characters.isNotEmpty()) {
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

                adapter.submitList(characters)

                setEmptyCollectionView(false)
            } else {
                setEmptyCollectionView(true)
            }
        }
    }

    private fun showQrCode() {
        QrCodeDialog(invitation, jsonMapper)
            .show(requireActivity().supportFragmentManager, QrCodeDialog::class.simpleName)
    }
}