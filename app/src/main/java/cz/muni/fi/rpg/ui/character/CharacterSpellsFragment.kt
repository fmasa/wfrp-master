package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.character.spells.SpellDialog
import cz.muni.fi.rpg.ui.character.spells.adapter.SpellAdapter
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.android.synthetic.main.fragment_character_spells.*
import kotlinx.android.synthetic.main.fragment_character_spells.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterSpellsFragment : Fragment(R.layout.fragment_character_spells),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterSpellsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)

    private val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addNewSpellButton.setOnClickListener { openSpellDialog(null) }

        spellsRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SpellAdapter(
            layoutInflater,
            { openSpellDialog(it) },
            { launch { viewModel.removeSpell(it) } }
        )

        spellsRecycler.adapter = adapter

        viewModel.spells.observe(viewLifecycleOwner) {
            val noSpells = it.isEmpty()

            adapter.submitList(it)

            view.noSpellsIcon.toggleVisibility(noSpells)
            view.noSpellsText.toggleVisibility(noSpells)
            view.spellsRecycler.toggleVisibility(!noSpells)
        }
    }

    private fun openSpellDialog(existingSpell: Spell?) {
        SpellDialog.newInstance(characterId, existingSpell)
            .show(childFragmentManager, "SpellDialog")
    }
}