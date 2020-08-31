package cz.muni.fi.rpg.ui.character.skills.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic

class SkillHolder(
    private val view: View,
    private val onClickListener: EntityListener<Skill>,
    private val onRemoveListener: EntityListener<Skill>
) : RecyclerView.ViewHolder(view) {
    fun bind(skill: Skill, stats: Stats) {
        val description = view.findViewById<TextView>(R.id.skillItemDescription)
        view.findViewById<TextView>(R.id.skillItemTitle).text = skill.name
        description.text = skill.description

        if (skill.description.isBlank()) {
            description.visibility = View.GONE

            val skillItemLayout = view.findViewById<ConstraintLayout>(R.id.skillItemLayout)
            ConstraintSet().apply {
                clone(skillItemLayout)
                connect(
                    R.id.skillItemTitle,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(skillItemLayout)
            }
        }

        view.findViewById<ImageView>(R.id.skillIcon).setImageResource(
            when (skill.characteristic) {
                SkillCharacteristic.AGILITY -> R.drawable.ic_agility
                SkillCharacteristic.BALLISTIC_SKILL -> R.drawable.ic_ballistic_skill
                SkillCharacteristic.DEXTERITY -> R.drawable.ic_dexterity
                SkillCharacteristic.INITIATIVE -> R.drawable.ic_initiative
                SkillCharacteristic.INTELLIGENCE -> R.drawable.ic_intelligence
                SkillCharacteristic.FELLOWSHIP -> R.drawable.ic_fellowship
                SkillCharacteristic.STRENGTH -> R.drawable.ic_strength
                SkillCharacteristic.TOUGHNESS -> R.drawable.ic_toughness
                SkillCharacteristic.WEAPON_SKILL -> R.drawable.ic_weapon_skill
                SkillCharacteristic.WILL_POWER -> R.drawable.ic_will_power
            }
        )

        view.findViewById<TextView>(R.id.skillTestNumberValue).text =
            (calculateBaseLevel(skill.characteristic, stats) + skill.advances).toString()

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(skill)

                    false
                }
        }
        view.setOnClickListener { onClickListener(skill) }
    }

    private fun calculateBaseLevel(characteristic: SkillCharacteristic, stats: Stats): Int {
        return when (characteristic) {
            SkillCharacteristic.AGILITY -> stats.agility
            SkillCharacteristic.BALLISTIC_SKILL -> stats.ballisticSkill
            SkillCharacteristic.DEXTERITY -> stats.dexterity
            SkillCharacteristic.FELLOWSHIP -> stats.fellowship
            SkillCharacteristic.INITIATIVE -> stats.initiative
            SkillCharacteristic.INTELLIGENCE -> stats.intelligence
            SkillCharacteristic.STRENGTH -> stats.strength
            SkillCharacteristic.TOUGHNESS -> stats.toughness
            SkillCharacteristic.WEAPON_SKILL -> stats.weaponSkill
            SkillCharacteristic.WILL_POWER -> stats.willPower
        }
    }
}