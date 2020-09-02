package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Character

sealed class Player {
    data class UserWithoutCharacter(val userId: String) : Player()
    data class ExistingCharacter(val character: Character) : Player()
}

class CharacterAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Player>
) : ListAdapter<Player, CharacterHolder>(
    object : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return when (oldItem) {
                is Player.UserWithoutCharacter -> when (newItem) {
                    is Player.UserWithoutCharacter -> oldItem.userId == newItem.userId
                    is Player.ExistingCharacter -> oldItem.userId == newItem.character.userId
                }
                is Player.ExistingCharacter -> when (newItem) {
                    is Player.UserWithoutCharacter -> oldItem.character.userId == newItem.userId
                    is Player.ExistingCharacter -> oldItem.character.id == newItem.character.id
                }
            }
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
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