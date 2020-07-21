package cz.muni.fi.rpg.ui.partyList.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.party.Party
import kotlinx.android.synthetic.main.party_item.view.*

class PartyHolder(
    private val view: View,
    private val userId: String,
    private val onClickListener: EntityListener<Party>,
    private val onRemoveListener: EntityListener<Party>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: Party) {
        view.party_item_title.text = item.getName()

        val playersCount = item.getPlayerCounts()

        if (playersCount > 0) {
            view.playersCount.text = playersCount.toString()
            view.playersCount.visibility = View.VISIBLE
            view.playersCountIcon.visibility = View.VISIBLE
        }

        view.setOnClickListener { onClickListener(item) }

        if (userId == item.gameMasterId || item.isSinglePlayer()) {
            view.setOnCreateContextMenuListener { menu, v, _ ->
                menu.add(0, v.id, 0, R.string.remove)
                    .setOnMenuItemClickListener {
                        onRemoveListener(item)

                        false
                    }
            }
        }
    }
}