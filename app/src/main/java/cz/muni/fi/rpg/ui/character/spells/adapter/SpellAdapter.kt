package cz.muni.fi.rpg.ui.character.spells.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.spells.Spell

class SpellAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Spell>,
    private val onRemoveListener: EntityListener<Spell>
) : ListAdapter<Spell, SpellHolder>(
    object : DiffUtil.ItemCallback<Spell>() {
        override fun areItemsTheSame(oldItem: Spell, newItem: Spell) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Spell, newItem: Spell) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellHolder {
        return SpellHolder(
            layoutInflater.inflate(R.layout.spell_item, parent, false),
            onClickListener = onClickListener,
            onRemoveListener = onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: SpellHolder, position: Int) {
        holder.bind(getItem(position))
    }
}