package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText

import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Race
import kotlinx.android.synthetic.main.fragment_character_info_creation.*
import kotlinx.android.synthetic.main.fragment_character_info_creation.view.*


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
            nextClicked(view)
        }
        setDefaultValues()
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

    fun setCharacterData(character: Character) {
        this.character = character
    }

    private fun setDefaultValues() {
        if (character == null) {
            return
        }
        view?.NameTextFill?.setText(character!!.getName())
        view?.CareerTextFill?.setText(character!!.getCareer())
        when (character!!.getRace()) {
            Race.HUMAN -> view?.radioButtonRaceHuman?.isChecked = true
            Race.DWARF -> view?.radioButtonRaceDwarf?.isChecked = true
            Race.ELF -> view?.radioButtonRaceElf?.isChecked = true
            Race.GNOME -> view?.radioButtonRaceGnome?.isChecked = true
            Race.HALFLING -> view?.radioButtonRaceHalfling?.isChecked = true
        }
        view?.button_next?.text = getString(R.string.button_edit_stats)
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

    private fun saveData(view: View) {
        val name = view.NameTextFill.text.toString()
        val career = view.CareerTextFill.text.toString()

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
