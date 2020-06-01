package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_character_spells.view.*

class CharacterSpellsFragment : Fragment(R.layout.fragment_character_spells) {
    private val viewModel: CharacterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.noSpellsIcon.visibility = View.VISIBLE;
        view.noSpellsText.visibility = View.VISIBLE;
    }
}