package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Character

class CharacterHolder(
    itemView: View,
    private val onClickListener: EntityListener<Player>,
    private val onRemoveListener: EntityListener<Character>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Player) {
        when (item) {
            is Player.UserWithoutCharacter -> {
                listOf(R.id.character_name, R.id.character_job, R.id.character_race)
                    .forEach { itemView.findViewById<View>(it).visibility = View.GONE }

                itemView.findViewById<View>(R.id.waitingForCharacter).visibility = View.VISIBLE
                itemView.setOnClickListener {  }
                itemView.isClickable = false
                itemView.isFocusable = false
            }
            is Player.ExistingCharacter -> {
                itemView.findViewById<TextView>(R.id.character_name).text = item.character.getName()
                itemView.findViewById<TextView>(R.id.character_race).setText(item.character.getRace().getReadableNameId())
                itemView.findViewById<TextView>(R.id.character_job).text = item.character.getCareer()
                itemView.isClickable = true
                itemView.isFocusable = true

                if (item.character.userId == null) {
                    itemView.setOnCreateContextMenuListener { menu, v, _ ->
                        menu.add(0, v.id, 0, R.string.remove)
                            .setOnMenuItemClickListener {
                                onRemoveListener(item.character)

                                false
                            }
                    }
                }
            }
        }

        itemView.setOnClickListener { onClickListener(item) }
    }
}