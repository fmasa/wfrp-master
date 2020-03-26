package cz.muni.fi.rpg.partyList.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.model.Party
import kotlinx.android.synthetic.main.party_item.view.*

class PartyHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v

    fun bind(item: Party) {
        view.party_item_title.text = item.name;
    }
}