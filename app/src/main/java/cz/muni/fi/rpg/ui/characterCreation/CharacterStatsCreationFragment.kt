package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.domain.character.Stats
import kotlinx.android.synthetic.main.fragment_character_stats_creation.*

class CharacterStatsCreationFragment : Fragment(R.layout.fragment_character_stats_creation) {
    lateinit var listener: CharacterStatsCreationListener

    var character: Character? = null

    interface CharacterStatsCreationListener {
        fun previousFragment()
        fun saveCharacter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_previous.setOnClickListener{
            listener.previousFragment()
        }

        button_finish.setOnClickListener{
            if(checkValues()) {
                listener.saveCharacter()
            }
        }

        setDefaultValues()
    }

    private fun checkValues(): Boolean {
        if (WeaponSkillTextFill.text.toString().isBlank()) WeaponSkillTextFill.setText("0")
        if (BallisticSkillTextFill.text.toString().isBlank()) BallisticSkillTextFill.setText("0")
        if (MagicTextFill.text.toString().isBlank()) MagicTextFill.setText("0")
        if (StrengthTextFill.text.toString().isBlank()) StrengthTextFill.setText("0")
        if (ToughnessTextFill.text.toString().isBlank()) ToughnessTextFill.setText("0")
        if (AgilityTextFill.text.toString().isBlank()) AgilityTextFill.setText("0")
        if (IntelligenceTextFill.text.toString().isBlank()) IntelligenceTextFill.setText("0")
        if (WillPowerTextFill.text.toString().isBlank()) WillPowerTextFill.setText("0")
        if (FellowshipTextFill.text.toString().isBlank()) FellowshipTextFill.setText("0")
        if (WoundsTextFill.text.toString().isBlank()) WoundsTextFill.setText("0")
        if (FateTextFill.text.toString().isBlank()) FateTextFill.setText("0")

        if (WeaponSkillTextFill.text.toString().toInt() > 100) { showError(WeaponSkillTextFill, 1); return false}
        if (BallisticSkillTextFill.text.toString().toInt() > 100) { showError(BallisticSkillTextFill, 1); return false}
        if (MagicTextFill.text.toString().toInt() > 100) { showError(MagicTextFill, 1); return false}
        if (StrengthTextFill.text.toString().toInt() > 100) { showError(StrengthTextFill, 1); return false}
        if (ToughnessTextFill.text.toString().toInt() > 100) { showError(ToughnessTextFill, 1); return false}
        if (AgilityTextFill.text.toString().toInt() > 100) { showError(AgilityTextFill, 1); return false}
        if (IntelligenceTextFill.text.toString().toInt() > 100) { showError(IntelligenceTextFill, 1); return false}
        if (WillPowerTextFill.text.toString().toInt() > 100) { showError(WillPowerTextFill, 1); return false}
        if (FellowshipTextFill.text.toString().toInt() > 100) { showError(FellowshipTextFill, 1); return false}
        if (WoundsTextFill.text.toString().toInt() > 100) { showError(WoundsTextFill, 1); return false}
        if (FateTextFill.text.toString().toInt() > 100) { showError(FateTextFill, 1); return false}

        if (WoundsTextFill.text.toString().toInt() == 0) {
            showError(WoundsTextFill, 0)
            return false
        }
        return true
    }

    fun setCharacterData(character: Character) {
        this.character = character
    }

    private fun setDefaultValues() {
        val character = this.character ?: return

        WeaponSkillTextFill.setText(character.getStats().weaponSkill.toString())
        BallisticSkillTextFill.setText(character.getStats().ballisticSkill.toString())
        StrengthTextFill.setText(character.getStats().strength.toString())
        ToughnessTextFill.setText(character.getStats().toughness.toString())
        FateTextFill.setText(character.getPoints().fate.toString())
        AgilityTextFill.setText(character.getStats().agility.toString())
        IntelligenceTextFill.setText(character.getStats().intelligence.toString())
        WillPowerTextFill.setText(character.getStats().willPower.toString())
        FellowshipTextFill.setText(character.getStats().fellowship.toString())
        WoundsTextFill.setText(character.getPoints().maxWounds.toString())
        MagicTextFill.setText(character.getStats().magic.toString())
        button_previous.text = getString(R.string.button_edit_info)
        button_finish.text = getString(R.string.button_submit)
    }

    private fun showError(input: EditText, type: Int) {
        if (type == 0) {
            input.error = (getString(R.string.error_value_is_0))
        } else {
            input.error = (getString(R.string.error_value_over_100))
        }
    }

    fun getData() : Pair <Stats,Points> {
        val stats = Stats(WeaponSkillTextFill.text.toString().toInt(), BallisticSkillTextFill.text.toString().toInt(),
            StrengthTextFill.text.toString().toInt(), ToughnessTextFill.text.toString().toInt(), AgilityTextFill.text.toString().toInt(),
            IntelligenceTextFill.text.toString().toInt(), WillPowerTextFill.text.toString().toInt(), FellowshipTextFill.text.toString().toInt(),
            MagicTextFill.text.toString().toInt())
        val points = Points(0, FateTextFill.text.toString().toInt(), FateTextFill.text.toString().toInt(),
            WoundsTextFill.text.toString().toInt(), WoundsTextFill.text.toString().toInt())

        return Pair(stats, points)
    }

    fun setCharacterStatsCreationListener(callback: CharacterStatsCreationListener): CharacterStatsCreationFragment {
        this.listener = callback
        return this
    }

}
