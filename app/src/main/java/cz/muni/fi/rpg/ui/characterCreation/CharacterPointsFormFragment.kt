package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_points_form.*

class CharacterPointsFormFragment :
    CharacterFormStep<CharacterPointsFormFragment.Data>(R.layout.fragment_character_points_form) {

    data class Data(
        val maxWounds: Int,
        val fate: Int,
        val resilience: Int
    )

    private lateinit var form: Form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form = Form(requireContext()).apply {
            addTextInput(maxWoundsInput).apply {
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
                addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            }

            addTextInput(fatePointsInput).apply {
                setDefaultValue("0")
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
            }

            addTextInput(resiliencePointsInput).apply {
                setDefaultValue("0")
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
            }
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