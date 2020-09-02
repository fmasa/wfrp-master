package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.EntityListener
import kotlinx.android.synthetic.main.character_item.view.*

class CharacterHolder(
    itemView: View,
    private val onClickListener: EntityListener<Player>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Player) {
        when (item) {
            is Player.UserWithoutCharacter -> {
                listOf(itemView.character_name, itemView.character_job, itemView.character_race)
                    .forEach { it.visibility = View.GONE }

                itemView.waitingForCharacter.visibility = View.VISIBLE
                itemView.setOnClickListener {  }
                itemView.isClickable = false
                itemView.isFocusable = false
            }
            is Player.ExistingCharacter -> {
                itemView.character_name.text = item.character.getName()
                itemView.character_race.setText(item.character.getRace().getReadableNameId())
                itemView.character_job.text = item.character.getCareer()
                itemView.isClickable = true
                itemView.isFocusable = true
            }
        }

        itemView.setOnClickListener { onClickListener(item) }
    }
}