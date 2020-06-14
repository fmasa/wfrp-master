package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel
import kotlinx.android.synthetic.main.fragment_character_misc.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class CharacterMiscFragment : Fragment(R.layout.fragment_character_misc) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterMiscFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val viewModel: CharacterMiscViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<CharacterId>(ARGUMENT_CHARACTER_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe(viewLifecycleOwner) { character ->
            characterAmbitionsCard.setValue(character.getAmbitions())

            characterAmbitionsCard.setOnClickListener { _ ->
                ChangeAmbitionsDialog
                    .newInstance(getString(R.string.title_character_ambitions), character.getAmbitions())
                    .setOnSaveListener { viewModel.updateCharacterAmbitions(it) }
                    .show(childFragmentManager, "ChangeAmbitionsDialog")
            }
        }

        viewModel.party.observe(viewLifecycleOwner) { party ->
            partyAmbitionsCard.setValue(party.getAmbitions())
        }
    }
}