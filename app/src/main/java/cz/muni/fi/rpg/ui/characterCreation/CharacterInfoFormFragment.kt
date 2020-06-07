package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Race
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_info_form.*

class CharacterInfoFormFragment : Fragment(R.layout.fragment_character_info_form) {
    var character : Character? = null

    data class CharacterInfo(var name: String, var race: Race, var career: String)

    private lateinit var form: Form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form = Form().apply {
            addTextInput(nameLayout).apply {
                setNotBlank(getString(R.string.error_cannot_be_empty))
                setMaxLength(Character.NAME_MAX_LENGTH)
            }

            addTextInput(careerLayout).apply {
                setNotBlank(getString(R.string.error_cannot_be_empty))
                setMaxLength(Character.CAREER_MAX_LENGTH)
            }
        }

        setDefaultValues()
    }

    fun submit(): CharacterInfo? {
        if (!form.validate()) {
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

        nameEditText.setText(character.getName())
        careerEditText.setText(character.getCareer())
        when (character.getRace()) {
            Race.HUMAN -> radioButtonRaceHuman.isChecked = true
            Race.DWARF -> radioButtonRaceDwarf.isChecked = true
            Race.ELF -> radioButtonRaceElf.isChecked = true
            Race.GNOME -> radioButtonRaceGnome.isChecked = true
            Race.HALFLING -> radioButtonRaceHalfling.isChecked = true
        }
    }

    private fun createCharacterInfo(): CharacterInfo {
        val name = nameEditText.text.toString()
        val career = careerEditText.text.toString()

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
