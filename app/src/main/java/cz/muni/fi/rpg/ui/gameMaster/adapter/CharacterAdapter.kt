package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.model.domain.character.Character

class CharacterAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: OnClickListener<Character>
) : ListAdapter<Character, CharacterHolder>(
    object:  DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem.userId == newItem.userId

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterHolder {
        val view = layoutInflater.inflate(R.layout.character_item, parent, false)
        return CharacterHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: CharacterHolder, position: Int) {
        holder.bind(getItem(position))
    }
}