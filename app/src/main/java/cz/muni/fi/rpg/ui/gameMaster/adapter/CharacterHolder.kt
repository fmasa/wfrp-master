package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.character.Character
import kotlinx.android.synthetic.main.character_item.view.*

class CharacterHolder(
    itemView: View,
    private val onClickListener: EntityListener<Character>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Player) {
        item.mapLeft {
            listOf(itemView.character_name, itemView.character_job, itemView.character_race)
                .forEach { it.visibility = View.GONE }

            itemView.waitingForCharacter.visibility = View.VISIBLE
            itemView.setOnClickListener {  }
            itemView.isClickable = false
            itemView.isFocusable = false
        }

        item.map {character ->
            itemView.character_name.text = character.getName()
            itemView.character_race.setText(character.getRace().getReadableNameId())
            itemView.character_job.text = character.getCareer()
            itemView.setOnClickListener { onClickListener(character) }
            itemView.isClickable = true
            itemView.isFocusable = true
        }
    }
}