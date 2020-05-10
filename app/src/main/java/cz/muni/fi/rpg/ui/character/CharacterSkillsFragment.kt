package cz.muni.fi.rpg.ui.character

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.CharacterViewModel

class CharacterSkillsFragment : Fragment(R.layout.fragment_character_skills) {
    private val viewModel: CharacterViewModel by activityViewModels()
}
