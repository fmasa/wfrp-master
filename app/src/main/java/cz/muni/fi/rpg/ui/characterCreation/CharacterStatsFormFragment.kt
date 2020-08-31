package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.views.TextInput

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
            view.findViewById<TextInput>(R.id.weaponSkillInput) to view.findViewById<TextInput>(R.id.maxWeaponSkillInput),
            view.findViewById<TextInput>(R.id.ballisticSkillInput) to view.findViewById<TextInput>(R.id.maxBallisticSkillInput),
            view.findViewById<TextInput>(R.id.strengthInput) to view.findViewById<TextInput>(R.id.maxStrengthInput),
            view.findViewById<TextInput>(R.id.toughnessInput) to view.findViewById<TextInput>(R.id.maxToughnessInput),
            view.findViewById<TextInput>(R.id.agilityInput) to view.findViewById<TextInput>(R.id.maxAgilityInput),
            view.findViewById<TextInput>(R.id.intelligenceInput) to view.findViewById<TextInput>(R.id.maxIntelligenceInput),
            view.findViewById<TextInput>(R.id.willPowerInput) to view.findViewById<TextInput>(R.id.maxWillPowerInput),
            view.findViewById<TextInput>(R.id.fellowshipInput) to view.findViewById<TextInput>(R.id.maxFellowshipInput),
            view.findViewById<TextInput>(R.id.initiativeInput) to view.findViewById<TextInput>(R.id.maxInitiativeInput),
            view.findViewById<TextInput>(R.id.dexterityInput) to view.findViewById<TextInput>(R.id.maxDexterityInput)
        )

        form = Form(requireContext())

        fields.values.forEach { input ->
            input.setEmptyValue("0")

            form.addTextInput(input).apply {
                setShowErrorInEditText()
                addLiveRule(R.string.error_value_over_100) { input.getValue().toInt() <= 100 }
            }
        }

        fields.entries.forEach { entry ->
            entry.key.setEmptyValue("0")

            form.addTextInput(entry.key).apply {
                setShowErrorInEditText()
                addLiveRule(R.string.error_value_over_100) { entry.key.getValue().toInt() <= 100 }
            }
        }

        setDefaultValues()
    }

    override fun submit(): CharacteristicsData? {
        if (!form.validate()) {
            return null
        }

        val view = requireView()

        return CharacteristicsData(
            base = Stats(
                agility = view.findViewById<TextInput>(R.id.agilityInput).getValue().toInt(),
                ballisticSkill = view.findViewById<TextInput>(R.id.ballisticSkillInput).getValue().toInt(),
                dexterity = view.findViewById<TextInput>(R.id.dexterityInput).getValue().toInt(),
                fellowship = view.findViewById<TextInput>(R.id.fellowshipInput).getValue().toInt(),
                initiative = view.findViewById<TextInput>(R.id.initiativeInput).getValue().toInt(),
                intelligence = view.findViewById<TextInput>(R.id.intelligenceInput).getValue().toInt(),
                strength = view.findViewById<TextInput>(R.id.strengthInput).getValue().toInt(),
                toughness = view.findViewById<TextInput>(R.id.toughnessInput).getValue().toInt(),
                weaponSkill = view.findViewById<TextInput>(R.id.weaponSkillInput).getValue().toInt(),
                willPower = view.findViewById<TextInput>(R.id.willPowerInput).getValue().toInt()
            ),
            advances = Stats(
                agility = view.findViewById<TextInput>(R.id.maxAgilityInput).getValue().toInt(),
                ballisticSkill = view.findViewById<TextInput>(R.id.maxBallisticSkillInput).getValue().toInt(),
                dexterity = view.findViewById<TextInput>(R.id.maxDexterityInput).getValue().toInt(),
                fellowship = view.findViewById<TextInput>(R.id.maxFellowshipInput).getValue().toInt(),
                initiative = view.findViewById<TextInput>(R.id.maxInitiativeInput).getValue().toInt(),
                intelligence = view.findViewById<TextInput>(R.id.maxIntelligenceInput).getValue().toInt(),
                strength = view.findViewById<TextInput>(R.id.maxStrengthInput).getValue().toInt(),
                toughness = view.findViewById<TextInput>(R.id.maxToughnessInput).getValue().toInt(),
                weaponSkill = view.findViewById<TextInput>(R.id.maxWeaponSkillInput).getValue().toInt(),
                willPower = view.findViewById<TextInput>(R.id.maxWillPowerInput).getValue().toInt()
            )
        )
    }

    override fun setCharacterData(character: Character) {
        this.character = character
        setDefaultValues()
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        val view = requireView()

        val fields = mapOf(
            (view.findViewById<TextInput>(R.id.weaponSkillInput) to view.findViewById<TextInput>(R.id.maxWeaponSkillInput)) to { stats: Stats -> stats.weaponSkill },
            (view.findViewById<TextInput>(R.id.ballisticSkillInput) to view.findViewById<TextInput>(R.id.maxBallisticSkillInput)) to { stats: Stats -> stats.ballisticSkill },
            (view.findViewById<TextInput>(R.id.strengthInput) to view.findViewById<TextInput>(R.id.maxStrengthInput)) to { stats: Stats -> stats.strength },
            (view.findViewById<TextInput>(R.id.toughnessInput) to view.findViewById<TextInput>(R.id.maxToughnessInput)) to { stats: Stats -> stats.toughness },
            (view.findViewById<TextInput>(R.id.agilityInput) to view.findViewById<TextInput>(R.id.maxAgilityInput)) to { stats: Stats -> stats.agility},
            (view.findViewById<TextInput>(R.id.intelligenceInput) to view.findViewById<TextInput>(R.id.maxIntelligenceInput)) to { stats: Stats -> stats.intelligence},
            (view.findViewById<TextInput>(R.id.willPowerInput) to view.findViewById<TextInput>(R.id.maxWillPowerInput)) to { stats: Stats -> stats.willPower},
            (view.findViewById<TextInput>(R.id.fellowshipInput) to view.findViewById<TextInput>(R.id.maxFellowshipInput)) to { stats: Stats -> stats.fellowship},
            (view.findViewById<TextInput>(R.id.initiativeInput) to view.findViewById<TextInput>(R.id.maxInitiativeInput)) to { stats: Stats -> stats.initiative},
            (view.findViewById<TextInput>(R.id.dexterityInput) to view.findViewById<TextInput>(R.id.maxDexterityInput)) to { stats: Stats -> stats.dexterity}
        )

        fields.forEach { entry ->
            val currentInput = entry.key.first
            val maxInput = entry.key.second

            currentInput.setDefaultValue(entry.value(character.getCharacteristicsBase()).toString(), force = true)
            maxInput.setDefaultValue(entry.value(character.getCharacteristicsAdvances()).toString(), force = true)
        }
    }
}
