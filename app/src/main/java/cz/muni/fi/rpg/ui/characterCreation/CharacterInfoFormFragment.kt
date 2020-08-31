package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Race
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.views.TextInput

class CharacterInfoFormFragment :
    CharacterFormStep<CharacterInfoFormFragment.Data>(R.layout.fragment_character_info_form) {
    companion object {
        private const val STATE_NAME = "infoName"
        private const val STATE_RACE = "infoRace"
        private const val STATE_CAREER = "infoCareer"
        private const val STATE_SOCIAL_CLASS = "infoClass"
        private const val STATE_PSYCHOLOGY = "infoPsychology"
        private const val STATE_MOTIVATION = "infoMotivation"
        private const val STATE_NOTE = "infoNote"
    }

    var character: Character? = null

    data class Data(
        val name: String,
        val race: Race,
        val career: String,
        val socialClass: String,
        val psychology: String,
        val motivation: String,
        val note: String
    )

    private lateinit var form: Form

    override fun onSaveInstanceState(outState: Bundle) {
        view?.let { view ->
            outState.putString(STATE_NAME, view.findViewById<TextInput>(R.id.nameInput).getValue())
            outState.putInt(STATE_RACE, view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId)
            outState.putString(STATE_CAREER, view.findViewById<TextInput>(R.id.careerInput).getValue())
            outState.putString(STATE_SOCIAL_CLASS, view.findViewById<TextInput>(R.id.socialClassInput).getValue())
            outState.putString(STATE_PSYCHOLOGY, view.findViewById<TextInput>(R.id.psychologyInput).getValue())
            outState.putString(STATE_MOTIVATION, view.findViewById<TextInput>(R.id.motivationInput).getValue())
            outState.putString(STATE_NOTE, view.findViewById<TextInput>(R.id.noteInput).getValue())
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form = Form(requireContext()).apply {
            addTextInput(view.findViewById<TextInput>(R.id.nameInput)).apply {
                setNotBlank(getString(R.string.error_cannot_be_empty))
                setMaxLength(Character.NAME_MAX_LENGTH, showCounter = false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.careerInput)).apply {
                setNotBlank(getString(R.string.error_cannot_be_empty))
                setMaxLength(Character.CAREER_MAX_LENGTH, showCounter = false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.socialClassInput)).apply {
                setNotBlank(getString(R.string.error_cannot_be_empty))
                setMaxLength(Character.SOCIAL_CLASS_MAX_LENGTH, showCounter = false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.psychologyInput)).apply {
                setMaxLength(Character.PSYCHOLOGY_MAX_LENGTH, showCounter = false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.motivationInput)).apply {
                setMaxLength(Character.MOTIVATION_MAX_LENGTH, showCounter = false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.noteInput)).apply {
                setMaxLength(Character.NOTE_MAX_LENGTH, showCounter = false)
            }
        }

        setDefaultValues()

        savedInstanceState?.let {
            it.getString(STATE_NAME)?.let { value -> view.findViewById<TextInput>(R.id.nameInput).setDefaultValue(value) }
            it.getInt(STATE_RACE).let(view.findViewById<RadioGroup>(R.id.radioGroup)::check)
            it.getString(STATE_CAREER)?.let { value -> view.findViewById<TextInput>(R.id.careerInput).setDefaultValue(value)}
            it.getString(STATE_SOCIAL_CLASS)?.let { value -> view.findViewById<TextInput>(R.id.socialClassInput).setDefaultValue(value) }
            it.getString(STATE_NOTE)?.let { value -> view.findViewById<TextInput>(R.id.noteInput).setDefaultValue(value) }
        }
    }

    override fun submit(): Data? {
        if (!form.validate()) {
            return null
        }

        return createCharacterInfo()
    }

    override fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        val view = requireView()

        view.findViewById<TextInput>(R.id.nameInput).setDefaultValue(character.getName())
        view.findViewById<TextInput>(R.id.careerInput).setDefaultValue(character.getCareer())
        view.findViewById<TextInput>(R.id.socialClassInput).setDefaultValue(character.getSocialClass())
        view.findViewById<TextInput>(R.id.psychologyInput).setDefaultValue(character.getPsychology())
        view.findViewById<TextInput>(R.id.motivationInput).setDefaultValue(character.getMotivation())
        view.findViewById<TextInput>(R.id.noteInput).setDefaultValue(character.getNote())

        when (character.getRace()) {
            Race.HUMAN -> view.findViewById<RadioButton>(R.id.radioButtonRaceHuman).isChecked = true
            Race.DWARF -> view.findViewById<RadioButton>(R.id.radioButtonRaceDwarf).isChecked = true
            Race.HIGH_ELF -> view.findViewById<RadioButton>(R.id.radioButtonRaceHighElf).isChecked = true
            Race.WOOD_ELF -> view.findViewById<RadioButton>(R.id.radioButtonRaceWoodElf).isChecked = true
            Race.HALFLING -> view.findViewById<RadioButton>(R.id.radioButtonRaceHalfling).isChecked = true
            Race.GNOME -> view.findViewById<RadioButton>(R.id.radioButtonRaceGnome).isChecked = true
        }
    }

    private fun createCharacterInfo(): Data {
        val view = requireView()

        val name = view.findViewById<TextInput>(R.id.nameInput).getValue()
        val career = view.findViewById<TextInput>(R.id.careerInput).getValue()
        val socialClass = view.findViewById<TextInput>(R.id.socialClassInput).getValue()

        val race: Race = when (view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
            R.id.radioButtonRaceHuman -> Race.HUMAN
            R.id.radioButtonRaceDwarf -> Race.DWARF
            R.id.radioButtonRaceHighElf -> Race.HIGH_ELF
            R.id.radioButtonRaceWoodElf -> Race.WOOD_ELF
            R.id.radioButtonRaceHalfling -> Race.HALFLING
            R.id.radioButtonRaceGnome -> Race.GNOME
            else -> error("No race selected")
        }

        return Data(
            name = name,
            race = race,
            career = career,
            socialClass = socialClass,
            psychology = view.findViewById<TextInput>(R.id.psychologyInput).getValue(),
            motivation = view.findViewById<TextInput>(R.id.motivationInput).getValue(),
            note = view.findViewById<TextInput>(R.id.noteInput).getValue()
        )
    }
}
