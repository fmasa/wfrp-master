package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_points_form.*

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
        outState.putString(STATE_MAX_WOUNDS, maxWoundsInput.getValue())
        outState.putString(STATE_FATE_POINTS, fatePointsInput.getValue())
        outState.putString(STATE_RESILIENCE_POINTS, resiliencePointsInput.getValue())

        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form = Form(requireContext()).apply {
            addTextInput(maxWoundsInput).apply {
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
                addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            }

            fatePointsInput.setEmptyValue("0")
            addTextInput(fatePointsInput).apply {
                addLiveRule(R.string.error_value_over_100) { fatePointsInput.getValue().toInt() <= 100 }
            }

            resiliencePointsInput.setEmptyValue("0")
            addTextInput(resiliencePointsInput).apply {
                addLiveRule(R.string.error_value_over_100) { resiliencePointsInput.getValue().toInt() <= 100 }
            }
        }

        savedInstanceState?.let {
            it.getString(STATE_MAX_WOUNDS)?.let { value -> maxWoundsInput.setDefaultValue(value) }
            it.getString(STATE_FATE_POINTS)?.let { value -> fatePointsInput.setDefaultValue(value) }
            it.getString(STATE_RESILIENCE_POINTS)?.let { value -> resiliencePointsInput.setDefaultValue(value) }
        }
    }

    override fun setCharacterData(character: Character) {
        val points = character.getPoints()

        maxWoundsInput.setDefaultValue(points.maxWounds.toString())
        fatePointsInput.setDefaultValue(points.fate.toString())
        resiliencePointsInput.setDefaultValue(points.resilience.toString())
    }

    override fun submit(): Data? {
        if (!form.validate()) {
            return null
        }

        return Data(
            maxWounds = maxWoundsInput.getValue().toInt(),
            fate = fatePointsInput.getValue().toInt(),
            resilience = resiliencePointsInput.getValue().toInt()
        )
    }
}