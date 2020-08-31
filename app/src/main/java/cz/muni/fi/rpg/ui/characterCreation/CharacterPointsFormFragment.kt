package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.views.TextInput

class CharacterPointsFormFragment :
    CharacterFormStep<CharacterPointsFormFragment.Data>(R.layout.fragment_character_points_form) {

    companion object {
        const val STATE_MAX_WOUNDS = "maxWounds"
        const val STATE_FATE_POINTS = "fatePoints"
        const val STATE_RESILIENCE_POINTS = "resiliencePoints"
    }

    data class Data(
        val maxWounds: Int,
        val fate: Int,
        val resilience: Int
    )

    private lateinit var form: Form

    override fun onSaveInstanceState(outState: Bundle) {
        view?.let {view ->
            outState.putString(STATE_MAX_WOUNDS, view.findViewById<TextInput>(R.id.maxWoundsInput).getValue())
            outState.putString(STATE_FATE_POINTS, view.findViewById<TextInput>(R.id.fatePointsInput).getValue())
            outState.putString(STATE_RESILIENCE_POINTS, view.findViewById<TextInput>(R.id.resiliencePointsInput).getValue())
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form = Form(requireContext()).apply {
            addTextInput(view.findViewById<TextInput>(R.id.maxWoundsInput)).apply {
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
                addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            }

            val fatePointsInput = view.findViewById<TextInput>(R.id.fatePointsInput)
            fatePointsInput.setEmptyValue("0")
            addTextInput(fatePointsInput).apply {
                addLiveRule(R.string.error_value_over_100) { fatePointsInput.getValue().toInt() <= 100 }
            }

            val resiliencePointsInput = view.findViewById<TextInput>(R.id.resiliencePointsInput)
            resiliencePointsInput.setEmptyValue("0")
            addTextInput(resiliencePointsInput).apply {
                addLiveRule(R.string.error_value_over_100) { resiliencePointsInput.getValue().toInt() <= 100 }
            }
        }

        savedInstanceState?.let {
            it.getString(STATE_MAX_WOUNDS)?.let { value -> view.findViewById<TextInput>(R.id.maxWoundsInput).setDefaultValue(value) }
            it.getString(STATE_FATE_POINTS)?.let { value -> view.findViewById<TextInput>(R.id.fatePointsInput).setDefaultValue(value) }
            it.getString(STATE_RESILIENCE_POINTS)?.let { value -> view.findViewById<TextInput>(R.id.resiliencePointsInput).setDefaultValue(value) }
        }
    }

    override fun setCharacterData(character: Character) {
        val points = character.getPoints()
        val view = requireView()

        view.findViewById<TextInput>(R.id.maxWoundsInput).setDefaultValue(points.maxWounds.toString())
        view.findViewById<TextInput>(R.id.fatePointsInput).setDefaultValue(points.fate.toString())
        view.findViewById<TextInput>(R.id.resiliencePointsInput).setDefaultValue(points.resilience.toString())
    }

    override fun submit(): Data? {
        if (!form.validate()) {
            return null
        }

        val view = requireView()

        return Data(
            maxWounds = view.findViewById<TextInput>(R.id.maxWoundsInput).getValue().toInt(),
            fate = view.findViewById<TextInput>(R.id.fatePointsInput).getValue().toInt(),
            resilience = view.findViewById<TextInput>(R.id.resiliencePointsInput).getValue().toInt()
        )
    }
}