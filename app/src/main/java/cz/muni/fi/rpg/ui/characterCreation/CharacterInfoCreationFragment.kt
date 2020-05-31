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
    var character : Character? = null

    data class CharacterInfo(var name: String, var race: Race, var career: String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultValues()
    }

    fun submit(): CharacterInfo? {
        showErrorIfNecessary(NameTextFill)
        showErrorIfNecessary(CareerTextFill)

        if (NameTextFill.text.isNullOrBlank() || CareerTextFill.text.isNullOrBlank()) {
            return null
        }

        return createCharacterInfo()
    }

    fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
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
    }

    private fun showErrorIfNecessary(input: EditText) {
        input.error =
        if (input.text.toString().isBlank()) {
            (getString(R.string.error_cannot_be_empty))
        } else null
    }

    private fun createCharacterInfo(): CharacterInfo {
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

        return CharacterInfo(name, race, career)
    }
}
