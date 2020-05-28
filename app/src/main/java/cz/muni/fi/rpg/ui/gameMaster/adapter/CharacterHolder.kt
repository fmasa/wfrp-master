package cz.muni.fi.rpg.ui.gameMaster.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.model.domain.character.Character
import kotlinx.android.synthetic.main.character_item.view.*

class CharacterHolder(
    itemView: View,
    private val onClickListener: OnClickListener<Character>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Character) {
        itemView.character_name.text = item.name
        itemView.character_race.text = item.race.name
        itemView.character_job.text = item.career

        itemView.setOnClickListener { onClickListener(item) };
    }
}