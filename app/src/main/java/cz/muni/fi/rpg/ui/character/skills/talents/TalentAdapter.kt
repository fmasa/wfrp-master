package cz.muni.fi.rpg.ui.character.skills.talents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.talents.Talent

class TalentAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Talent>,
    private val onRemoveListener: EntityListener<Talent>
) : ListAdapter<Talent, TalentHolder>(
    object : DiffUtil.ItemCallback<Talent>() {
        override fun areItemsTheSame(oldItem: Talent, newItem: Talent) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Talent, newItem: Talent) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalentHolder {
        return TalentHolder(
            layoutInflater.inflate(R.layout.talent_item, parent, false),
            onClickListener,
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: TalentHolder, position: Int) {
        holder.bind(getItem(position))
    }
}