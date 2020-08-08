package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_stats_form.*

class CharacterStatsFormFragment :
    CharacterFormStep<CharacterStatsFormFragment.CharacteristicsData>(R.layout.fragment_character_stats_form) {

    data class CharacteristicsData(
        val base: Stats,
        val advances: Stats
    )

    var character: Character? = null

    private lateinit var form: Form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fields = mapOf(
            weaponSkillInput to maxWeaponSkillInput,
            ballisticSkillInput to maxBallisticSkillInput,
            strengthInput to maxStrengthInput,
            toughnessInput to maxToughnessInput,
            agilityInput to maxAgilityInput,
            intelligenceInput to maxIntelligenceInput,
            willPowerInput to maxWillPowerInput,
            fellowshipInput to maxFellowshipInput,
            initiativeInput to maxInitiativeInput,
            dexterityInput to maxDexterityInput
        )

        form = Form(requireContext())

        fields.values.forEach { input ->
            form.addTextInput(input).apply {
                setDefaultValue("0")
                setShowErrorInEditText()
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
            }
        }

        fields.entries.forEach { entry ->
            form.addTextInput(entry.key).apply {
                setDefaultValue("0")
                setShowErrorInEditText()
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
            }
        }

        setDefaultValues()
    }

    override fun submit(): CharacteristicsData? {
        if (!form.validate()) {
            return null
        }

        return CharacteristicsData(
            base = Stats(
                agility = agilityInput.getValue().toInt(),
                ballisticSkill = ballisticSkillInput.getValue().toInt(),
                dexterity = dexterityInput.getValue().toInt(),
                fellowship = fellowshipInput.getValue().toInt(),
                initiative = initiativeInput.getValue().toInt(),
                intelligence = intelligenceInput.getValue().toInt(),
                strength = strengthInput.getValue().toInt(),
                toughness = toughnessInput.getValue().toInt(),
                weaponSkill = weaponSkillInput.getValue().toInt(),
                willPower = willPowerInput.getValue().toInt()
            ),
            advances = Stats(
                agility = maxAgilityInput.getValue().toInt(),
                ballisticSkill = maxBallisticSkillInput.getValue().toInt(),
                dexterity = maxDexterityInput.getValue().toInt(),
                fellowship = maxFellowshipInput.getValue().toInt(),
                initiative = maxInitiativeInput.getValue().toInt(),
                intelligence = maxIntelligenceInput.getValue().toInt(),
                strength = maxStrengthInput.getValue().toInt(),
                toughness = maxToughnessInput.getValue().toInt(),
                weaponSkill = maxWeaponSkillInput.getValue().toInt(),
                willPower = maxWillPowerInput.getValue().toInt()
            )
        )
    }

    override fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        val fields = mapOf(
            (weaponSkillInput to maxWeaponSkillInput) to { stats: Stats -> stats.weaponSkill },
            (ballisticSkillInput to maxBallisticSkillInput) to { stats: Stats -> stats.ballisticSkill },
            (strengthInput to maxStrengthInput) to { stats: Stats -> stats.strength },
            (toughnessInput to maxToughnessInput) to { stats: Stats -> stats.toughness },
            (agilityInput to maxAgilityInput) to { stats: Stats -> stats.agility},
            (intelligenceInput to maxIntelligenceInput) to { stats: Stats -> stats.intelligence},
            (willPowerInput to maxWillPowerInput) to { stats: Stats -> stats.willPower},
            (fellowshipInput to maxFellowshipInput) to { stats: Stats -> stats.fellowship},
            (initiativeInput to maxInitiativeInput) to { stats: Stats -> stats.initiative},
            (dexterityInput to maxDexterityInput) to { stats: Stats -> stats.dexterity}
        )

        fields.forEach { entry ->
            val currentInput = entry.key.first
            val maxInput = entry.key.second

            currentInput.setDefaultValue(entry.value(character.getCharacteristicsBase()).toString(), force = true)
            maxInput.setDefaultValue(entry.value(character.getCharacteristicsAdvances()).toString(), force = true)
        }
    }
}
