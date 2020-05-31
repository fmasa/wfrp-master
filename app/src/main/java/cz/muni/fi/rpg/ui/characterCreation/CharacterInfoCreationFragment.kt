package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Race
import kotlinx.android.synthetic.main.fragment_character_info_creation.*

class CharacterInfoCreationFragment : Fragment(R.layout.fragment_character_info_creation) {
    lateinit var listener: CharacterInfoCreationListener

    private lateinit var characterInfo: CharacterInfo

    var character : Character? = null

    interface CharacterInfoCreationListener {
        fun nextFragment()
    }

    data class CharacterInfo(var name: String, var race: Race, var career: String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_next.setOnClickListener{
            nextClicked()
        }
        setDefaultValues()
    }

    private fun nextClicked() {
        showErrorIfNecessary(NameTextFill)
        showErrorIfNecessary(CareerTextFill)
        if (NameTextFill.text.isNullOrBlank() || CareerTextFill.text.isNullOrBlank()) {
            return
        }
        saveData()
        listener.nextFragment()
    }

    fun setCharacterData(character: Character) {
        this.character = character
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        NameTextFill.setText(character.getName())
        CareerTextFill.setText(character.getCareer())
        when (character.getRace()) {
            Race.HUMAN -> radioButtonRaceHuman.isChecked = true
            Race.DWARF -> radioButtonRaceDwarf.isChecked = true
            Race.ELF -> radioButtonRaceElf.isChecked = true
            Race.GNOME -> radioButtonRaceGnome.isChecked = true
            Race.HALFLING -> radioButtonRaceHalfling.isChecked = true
        }

        button_next.text = getString(R.string.button_edit_stats)
    }

    private fun showErrorIfNecessary(input: EditText) {
        input.error =
        if (input.text.toString().isBlank()) {
            (getString(R.string.error_cannot_be_empty))
        } else null
    }

    fun setCharacterInfoCreationListener(callback: CharacterInfoCreationListener): CharacterInfoCreationFragment {
        this.listener = callback
        return this
    }

    private fun saveData() {
        val name = NameTextFill.text.toString()
        val career = CareerTextFill.text.toString()

        val race: Race = when(radioGroup.checkedRadioButtonId) {
            R.id.radioButtonRaceHuman -> Race.HUMAN
            R.id.radioButtonRaceDwarf -> Race.DWARF
            R.id.radioButtonRaceElf -> Race.ELF
            R.id.radioButtonRaceGnome -> Race.GNOME
            R.id.radioButtonRaceHalfling -> Race.HALFLING
            else -> error("No race selected")
        }

        characterInfo = CharacterInfo(name, race, career)
    }

    fun getData() : CharacterInfo {
        return characterInfo
    }
}
