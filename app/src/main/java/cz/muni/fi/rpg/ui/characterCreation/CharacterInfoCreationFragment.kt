package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Race
import kotlinx.android.synthetic.main.fragment_character_info_creation.view.*


class CharacterInfoCreationFragment : Fragment() {
    lateinit var listener: CharacterInfoCreationListener

    public interface CharacterInfoCreationListener {
        fun switchFragment(id: Number)
    }

    lateinit var characterName: EditText
    lateinit var characterCarrer: EditText
    lateinit var characterRace: Race
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_character_info_creation, container, false)
        val activity = activity

        characterName = v.NameTextFill
        characterCarrer = v.CareerTextFill
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
            listener.switchFragment(1)
        }
        return v
    }

    fun setCharacterInfoCreationListener(callback: CharacterInfoCreationListener) {
        this.listener = callback
    }

}
