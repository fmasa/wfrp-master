package cz.muni.fi.rpg.ui.character.skills.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.skills.Skill

class SkillAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Skill>,
    private val onRemoveListener: EntityListener<Skill>
) : ListAdapter<Skill, SkillHolder>(
    object : DiffUtil.ItemCallback<Skill>() {
        override fun areItemsTheSame(oldItem: Skill, newItem: Skill) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Skill, newItem: Skill) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillHolder {
        return SkillHolder(
            layoutInflater.inflate(R.layout.skill_item, parent, false),
            onClickListener,
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        holder.bind(getItem(position))
    }
}