package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.character.spells.SpellDialog
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.android.synthetic.main.fragment_character_spells.*
import kotlinx.android.synthetic.main.fragment_character_spells.view.*
import kotlinx.android.synthetic.main.fragment_character_spells.view.addNewSpellButton
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterSpellsFragment : Fragment(R.layout.fragment_character_spells) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSpellsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by lazy {
        requireArguments().getParcelable<CharacterId>(ARGUMENT_CHARACTER_ID)
            ?: error("Character ID not set")
    }

    private val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addNewSpellButton.setOnClickListener {
            SpellDialog.newInstance(characterId, null)
                .show(childFragmentManager, "SpellDialog")
        }

        viewModel.spells.observe(viewLifecycleOwner) {
            val noSpells = it.isEmpty()

            view.noSpellsIcon.toggleVisibility(noSpells)
            view.noSpellsText.toggleVisibility(noSpells)
        }
    }
}