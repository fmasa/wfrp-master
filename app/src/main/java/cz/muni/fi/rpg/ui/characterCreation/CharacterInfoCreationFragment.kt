package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Race
import kotlinx.android.synthetic.main.fragment_character_info_creation.*
import kotlinx.android.synthetic.main.fragment_character_info_creation.view.*

data class CharacterInfo(var name: String, var race: Race, var career: String)

class CharacterInfoCreationFragment : Fragment(R.layout.fragment_character_info_creation) {
    lateinit var listener: CharacterInfoCreationListener

    lateinit var characterInfo: CharacterInfo

    public interface CharacterInfoCreationListener {
        fun nextFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_next.setOnClickListener{
            nextClicked(view)
        }
    }

    private fun nextClicked(view: View) {
        showErrorIfNecessary(view.NameTextFill)
        showErrorIfNecessary(view.CareerTextFill)
        if (view.NameTextFill.text.isNullOrBlank() || view.CareerTextFill.text.isNullOrBlank()) {
            return
        }
        saveData(view)
        listener.nextFragment()
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

    private fun saveData(view: View) {
        val name = view.NameTextFill.text.toString()
        var race = Race.HUMAN
        val career = view.CareerTextFill.text.toString()

        if (view.radioButtonRaceHuman.isChecked)
            race = Race.HUMAN
        if (view.radioButtonRaceDwarf.isChecked)
            race = Race.DWARF
        if (view.radioButtonRaceElf.isChecked)
            race = Race.ELF
        if (view.radioButtonRaceGnome.isChecked)
            race = Race.GNOME
        if (view.radioButtonRaceHalfling.isChecked)
            race = Race.HALFLING

        characterInfo = CharacterInfo(name, race, career)
    }

    fun getData() : CharacterInfo {
        return characterInfo
    }
}
