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
import kotlinx.android.synthetic.main.fragment_character_stats_creation.view.*

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
            if(checkValues(view)) {
                listener.saveCharacter()
            }
        }

        setDefaultValues()
    }

    private fun checkValues(view: View): Boolean {
        if (view.WeaponSkillTextFill.text.toString().isBlank()) view.WeaponSkillTextFill .setText("0")
        if (view.BallisticSkillTextFill.text.toString().isBlank()) view.BallisticSkillTextFill.setText("0")
        if (view.MagicTextFill.text.toString().isBlank()) view.MagicTextFill.setText("0")
        if (view.StrengthTextFill.text.toString().isBlank()) view.StrengthTextFill.setText("0")
        if (view.ToughnessTextFill.text.toString().isBlank()) view.ToughnessTextFill.setText("0")
        if (view.AgilityTextFill.text.toString().isBlank()) view.AgilityTextFill.setText("0")
        if (view.IntelligenceTextFill.text.toString().isBlank()) view.IntelligenceTextFill.setText("0")
        if (view.WillPowerTextFill.text.toString().isBlank()) view.WillPowerTextFill.setText("0")
        if (view.FellowshipTextFill.text.toString().isBlank()) view.FellowshipTextFill.setText("0")
        if (view.WoundsTextFill.text.toString().isBlank()) view.WoundsTextFill.setText("0")
        if (view.FateTextFill.text.toString().isBlank()) view.FateTextFill.setText("0")

        if (view.WeaponSkillTextFill.text.toString().toInt() > 100) { showError(view.WeaponSkillTextFill, 1); return false}
        if (view.BallisticSkillTextFill.text.toString().toInt() > 100) { showError(view.BallisticSkillTextFill, 1); return false}
        if (view.MagicTextFill.text.toString().toInt() > 100) { showError(view.MagicTextFill, 1); return false}
        if (view.StrengthTextFill.text.toString().toInt() > 100) { showError(view.StrengthTextFill, 1); return false}
        if (view.ToughnessTextFill.text.toString().toInt() > 100) { showError(view.ToughnessTextFill, 1); return false}
        if (view.AgilityTextFill.text.toString().toInt() > 100) { showError(view.AgilityTextFill, 1); return false}
        if (view.IntelligenceTextFill.text.toString().toInt() > 100) { showError(view.IntelligenceTextFill, 1); return false}
        if (view.WillPowerTextFill.text.toString().toInt() > 100) { showError(view.WillPowerTextFill, 1); return false}
        if (view.FellowshipTextFill.text.toString().toInt() > 100) { showError(view.FellowshipTextFill, 1); return false}
        if (view.WoundsTextFill.text.toString().toInt() > 100) { showError(view.WoundsTextFill, 1); return false}
        if (view.FateTextFill.text.toString().toInt() > 100) { showError(view.FateTextFill, 1); return false}

        if (view.WoundsTextFill.text.toString().toInt() == 0) {
            showError(view.WoundsTextFill, 0)
            return false
        }
        return true
    }

    fun setCharacterData(character: Character) {
        this.character = character
    }

    private fun setDefaultValues() {
        if (character == null) {
            return
        }
        view?.WeaponSkillTextFill?.setText(character!!.getStats().weaponSkill.toString())
        view?.BallisticSkillTextFill?.setText(character!!.getStats().ballisticSkill.toString())
        view?.StrengthTextFill?.setText(character!!.getStats().strength.toString())
        view?.ToughnessTextFill?.setText(character!!.getStats().toughness.toString())
        view?.FateTextFill?.setText(character!!.getPoints().fate.toString())
        view?.AgilityTextFill?.setText(character!!.getStats().agility.toString())
        view?.IntelligenceTextFill?.setText(character!!.getStats().intelligence.toString())
        view?.WillPowerTextFill?.setText(character!!.getStats().willPower.toString())
        view?.FellowshipTextFill?.setText(character!!.getStats().fellowship.toString())
        view?.WoundsTextFill?.setText(character!!.getPoints().maxWounds.toString())
        view?.MagicTextFill?.setText(character!!.getStats().magic.toString())
        view?.button_previous?.text = getString(R.string.button_edit_info)
        view?.button_finish?.text = getString(R.string.button_submit)
    }

    private fun showError(input: EditText, type: Int) {
        if (type == 0) {
            input.error = (getString(R.string.error_value_is_0))
        } else {
            input.error = (getString(R.string.error_value_over_100))
        }

    }

    fun getData() : Pair <Stats,Points> {
        val stats = Stats(view?.WeaponSkillTextFill?.text.toString().toInt(), view?.BallisticSkillTextFill?.text.toString().toInt(),
            view?.StrengthTextFill?.text.toString().toInt(), view?.ToughnessTextFill?.text.toString().toInt(), view?.AgilityTextFill?.text.toString().toInt(),
            view?.IntelligenceTextFill?.text.toString().toInt(), view?.WillPowerTextFill?.text.toString().toInt(), view?.FellowshipTextFill?.text.toString().toInt(),
            view?.MagicTextFill?.text.toString().toInt())
        val points = Points(0, view?.FateTextFill?.text.toString().toInt(), view?.FateTextFill?.text.toString().toInt(),
            view?.WoundsTextFill?.text.toString().toInt(), view?.WoundsTextFill?.text.toString().toInt())

        return Pair(stats, points)
    }

    fun setCharacterStatsCreationListener(callback: CharacterStatsCreationListener): CharacterStatsCreationFragment {
        this.listener = callback
        return this
    }

}
