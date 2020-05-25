package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Race
import kotlinx.android.synthetic.main.fragment_character_info_creation.view.*

class CharacterInfoCreationFragment : Fragment() {
    lateinit var listener: CharacterInfoCreationListener

    public interface CharacterInfoCreationListener {
        fun switchFragment(id: Number)
    }

    lateinit var characterName: EditText
    lateinit var characterCareer: EditText
    lateinit var characterRace: Race
    private lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_character_info_creation, container, false)

        characterName = v.NameTextFill
        characterCareer = v.CareerTextFill
        if (v.radioButtonRaceHuman.isChecked)
            characterRace = Race.HUMAN
        if (v.radioButtonRaceDwarf.isChecked)
            characterRace = Race.DWARF
        if (v.radioButtonRaceElf.isChecked)
            characterRace = Race.ELF
        if (v.radioButtonRaceGnome.isChecked)
            characterRace = Race.GNOME
        if (v.radioButtonRaceHalfling.isChecked)
            characterRace = Race.HALFLING
        nextButton = v.button_next

        nextButton.setOnClickListener{
            nextClicked() }

        return v
    }

    private fun nextClicked() {
        showErrorIfNecessary(characterName)
        showErrorIfNecessary(characterCareer)
        if (characterName.text.isNullOrBlank() || characterCareer.text.isNullOrBlank()) {
            return
        }
        listener.switchFragment(1)
    }

    private fun showErrorIfNecessary(input: EditText) {
        input.error =
        if (input.text.toString().isBlank()) {
            ("Cannot be empty!")
        } else null
    }

    fun setCharacterInfoCreationListener(callback: CharacterInfoCreationListener): CharacterInfoCreationFragment {
        this.listener = callback
        return this
    }

}
