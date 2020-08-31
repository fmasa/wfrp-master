package cz.muni.fi.rpg.ui.character.skills.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.common.DiffCallback

class SkillAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Skill>,
    private val onRemoveListener: EntityListener<Skill>
) : ListAdapter<Pair<Skill, Stats>, SkillHolder>(
    DiffCallback({a, b -> a.first.id == b.first.id}, {a, b -> a == b})
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillHolder {
        return SkillHolder(
            layoutInflater.inflate(R.layout.skill_item, parent, false),
            onClickListener,
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.first, item.second)
    }
}