package cz.muni.fi.rpg.ui.character.skills.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic
import kotlinx.android.synthetic.main.skill_item.view.*

class SkillHolder(
    private val view: View,
    private val onClickListener: OnClickListener<Skill>
) : RecyclerView.ViewHolder(view) {
    fun bind(skill: Skill) {
        view.skillItemTitle.text = skill.name;
        view.skillItemDescription.text = skill.description

        view.skillIcon.setImageResource(
            when (skill.characteristic) {
                SkillCharacteristic.AGILITY -> R.drawable.ic_agility
                SkillCharacteristic.INTELLIGENCE -> R.drawable.ic_intelligence
                SkillCharacteristic.FELLOWSHIP -> R.drawable.ic_fellowship
                SkillCharacteristic.STRENGTH -> R.drawable.ic_strength
                SkillCharacteristic.TOUGHNESS -> R.drawable.ic_toughness
                SkillCharacteristic.WILL_POWER -> R.drawable.ic_will_power
            }
        )

        view.setOnClickListener { onClickListener(skill) };
    }
}