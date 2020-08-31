package cz.muni.fi.rpg.ui.partyList.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.party.Party

class PartyHolder(
    private val view: View,
    private val userId: String,
    private val onClickListener: EntityListener<Party>,
    private val onRemoveListener: EntityListener<Party>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: Party) {
        view.findViewById<TextView>(R.id.party_item_title).text = item.getName()

        val playersCount = item.getPlayerCounts()

        if (playersCount > 0) {
            val playersCountView = view.findViewById<TextView>(R.id.playersCount)
            playersCountView.text = playersCount.toString()
            playersCountView.visibility = View.VISIBLE
            view.findViewById<View>(R.id.playersCountIcon).visibility = View.VISIBLE
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