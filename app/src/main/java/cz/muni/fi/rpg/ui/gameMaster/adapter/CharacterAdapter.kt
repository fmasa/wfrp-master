package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import arrow.core.Either
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Character

typealias Player = Either<PlayerWithoutCharacter, Character>

class CharacterAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Character>
) : ListAdapter<Player, CharacterHolder>(
    object : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return extractUserId(oldItem) == extractUserId(newItem)
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.isLeft() == newItem.isLeft() &&
                    comparableHash(oldItem) == comparableHash(newItem)
        }

        private fun extractUserId(player: Player): String {
            return player.fold({ it.userId }, { it.userId })
        }

        private fun comparableHash(player: Player): Int {
           return player.fold({ it.hashCode() }, { it.hashCode() })
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