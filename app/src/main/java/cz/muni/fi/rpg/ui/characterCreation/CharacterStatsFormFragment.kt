package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_stats_form.*

class CharacterStatsFormFragment :
    CharacterFormStep<Stats>(R.layout.fragment_character_stats_form) {
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
            fellowshipInput
        )

        form = Form(requireContext())

        fields.forEach { input ->
            input.setDefaultValue("0")
            form.addTextInput(input).apply {
                addLiveRule(getString(R.string.error_required)) { !it.isNullOrBlank() }
                addLiveRule(getString(R.string.error_value_over_100)) {
                    it.toString().toInt() <= 100
                }
            }
        }

        setDefaultValues()
    }

    override fun submit(): Stats? {
        if (!form.validate()) {
            return null
        }

        return Stats(
            agility = agilityInput.getValue().toInt(),
            ballisticSkill = ballisticSkillInput.getValue().toInt(),
            dexterity = 0,
            fellowship = fellowshipInput.getValue().toInt(),
            initiative = 0,
            intelligence = intelligenceInput.getValue().toInt(),
            strength = strengthInput.getValue().toInt(),
            toughness = toughnessInput.getValue().toInt(),
            weaponSkill = weaponSkillInput.getValue().toInt(),
            willPower = willPowerInput.getValue().toInt()
        )
    }

    override fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        weaponSkillInput.setDefaultValue(character.getStats().weaponSkill.toString())
        ballisticSkillInput.setDefaultValue(character.getStats().ballisticSkill.toString())
        strengthInput.setDefaultValue(character.getStats().strength.toString())
        toughnessInput.setDefaultValue(character.getStats().toughness.toString())
        agilityInput.setDefaultValue(character.getStats().agility.toString())
        intelligenceInput.setDefaultValue(character.getStats().intelligence.toString())
        willPowerInput.setDefaultValue(character.getStats().willPower.toString())
        fellowshipInput.setDefaultValue(character.getStats().fellowship.toString())
    }
}
