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

    lateinit var previousButton: Button
    lateinit var finishButton: Button
    lateinit var weaponSkill: EditText
    lateinit var ballisticSkill: EditText
    lateinit var magic: EditText
    lateinit var strength: EditText
    lateinit var toughness: EditText
    lateinit var wounds: EditText
    lateinit var agility: EditText
    lateinit var intelligence: EditText
    lateinit var willPower: EditText
    lateinit var fellowship: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_character_stats_creation, container, false)
        val activity = activity

        previousButton = v.button_previous
        finishButton = v.button_finish
        weaponSkill = v.WeaponSkillTextFill
        ballisticSkill = v.BallisticSkillTextFill
        magic = v.MagicTextFill
        strength = v.StrengthTextFill
        toughness = v.ToughnessTextFill
        wounds = v.WoundsTextFill
        agility = v.AgilityTextFill
        intelligence = v.IntelligenceTextFill
        willPower = v.WillPowerTextFill
        fellowship = v.FellowshipTextFill

        previousButton.setOnClickListener{
            listener.switchFragment(0)
            //(activity as CharacterCreationActivity).switchFragment(0)
        }

        finishButton.setOnClickListener{
            listener.saveCharacter()
        }
        return v
    }

    fun setCharacterStatsCreationListener(callback: CharacterStatsCreationListener) {
        this.listener = callback
    }

}
