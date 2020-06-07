package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_stats_form.*

internal data class CharacterStatsData(
    val stats: Stats,
    val maxWounds: Int,
    val fatePoints: Int
)

class CharacterStatsFormFragment : Fragment(R.layout.fragment_character_stats_form) {
    var character: Character? = null

    private lateinit var form: Form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fields = arrayOf(
            weaponSkillInput,
            ballisticSkillInput,
            strengthInput,
            toughnessInput,
            agilityInput,
            intelligenceInput,
            willPowerInput,
            fellowshipInput,
            woundsInput,
            fatePointsInput,
            magicInput
        )

        form = Form()

        fields.forEach { input ->
            input.setDefaultValue("0")
            form.addTextInput(input).apply {
                addLiveRule(getString(R.string.error_required)) { !it.isNullOrBlank() }
                addLiveRule(getString(R.string.error_value_over_100)) {
                    it.toString().toInt() <= 100
                }
            }
        }

        form.getInput(R.id.woundsInput)
            .addLiveRule(getString(R.string.error_value_is_0)) { it.toString().toInt() > 0}

        setDefaultValues()
    }

    internal fun submit(): CharacterStatsData? {
        if (!form.validate()) {
            return null
        }

        return CharacterStatsData(
            Stats(
                weaponSkillInput.getValue().toInt(),
                ballisticSkillInput.getValue().toInt(),
                strengthInput.getValue().toInt(),
                toughnessInput.getValue().toInt(),
                agilityInput.getValue().toInt(),
                intelligenceInput.getValue().toInt(),
                willPowerInput.getValue().toInt(),
                fellowshipInput.getValue().toInt(),
                magicInput.getValue().toInt()
            ),
            woundsInput.getValue().toInt(),
            fatePointsInput.getValue().toInt()
        )
    }

    fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        weaponSkillInput.setDefaultValue(character.getStats().weaponSkill.toString())
        ballisticSkillInput.setDefaultValue(character.getStats().ballisticSkill.toString())
        strengthInput.setDefaultValue(character.getStats().strength.toString())
        toughnessInput.setDefaultValue(character.getStats().toughness.toString())
        fatePointsInput.setDefaultValue(character.getPoints().fate.toString())
        agilityInput.setDefaultValue(character.getStats().agility.toString())
        intelligenceInput.setDefaultValue(character.getStats().intelligence.toString())
        willPowerInput.setDefaultValue(character.getStats().willPower.toString())
        fellowshipInput.setDefaultValue(character.getStats().fellowship.toString())
        woundsInput.setDefaultValue(character.getPoints().maxWounds.toString())
        magicInput.setDefaultValue(character.getStats().magic.toString())
    }
}
