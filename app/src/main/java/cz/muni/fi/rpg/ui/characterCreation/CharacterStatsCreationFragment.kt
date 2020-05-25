package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.fragment_character_stats_creation.view.*

class CharacterStatsCreationFragment : Fragment() {
    lateinit var listener: CharacterStatsCreationListener

    public interface CharacterStatsCreationListener {
        fun switchFragment(id: Number)
        fun saveCharacter()
    }

    private lateinit var previousButton: Button
    private lateinit var finishButton: Button
    lateinit var weaponSkill: EditText
    lateinit var ballisticSkill: EditText
    lateinit var magic: EditText
    lateinit var strength: EditText
    lateinit var toughness: EditText
    lateinit var agility: EditText
    lateinit var intelligence: EditText
    lateinit var willPower: EditText
    lateinit var fellowship: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_character_stats_creation, container, false)

        previousButton = v.button_previous
        finishButton = v.button_finish
        weaponSkill = v.WeaponSkillTextFill
        ballisticSkill = v.BallisticSkillTextFill
        magic = v.MagicTextFill
        strength = v.StrengthTextFill
        toughness = v.ToughnessTextFill
        agility = v.AgilityTextFill
        intelligence = v.IntelligenceTextFill
        willPower = v.WillPowerTextFill
        fellowship = v.FellowshipTextFill

        previousButton.setOnClickListener{
            listener.switchFragment(0)
        }

        finishButton.setOnClickListener{
            checkValues()
            listener.saveCharacter()
        }
        return v
    }

    private fun checkValues() {
        if (weaponSkill.text.toString().isBlank()) weaponSkill.setText("0")
        if (ballisticSkill.text.toString().isBlank()) ballisticSkill.setText("0")
        if (magic.text.toString().isBlank()) magic.setText("0")
        if (strength.text.toString().isBlank()) strength.setText("0")
        if (toughness.text.toString().isBlank()) toughness.setText("0")
        if (agility.text.toString().isBlank()) agility.setText("0")
        if (intelligence.text.toString().isBlank()) intelligence.setText("0")
        if (willPower.text.toString().isBlank()) willPower.setText("0")
        if (fellowship.text.toString().isBlank()) fellowship.setText("0")
    }

    fun setCharacterStatsCreationListener(callback: CharacterStatsCreationListener): CharacterStatsCreationFragment {
        this.listener = callback
        return this
    }

}
