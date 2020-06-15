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
    fun bind(item: Character) {
        itemView.character_name.text = item.getName()
        itemView.character_race.setText(item.getRace().getReadableNameId())
        itemView.character_job.text = item.getCareer()
        itemView.setOnClickListener { onClickListener(item) }
    }
}