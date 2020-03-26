package cz.muni.fi.rpg.partyList.adapter

import android.view.View
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.model.Party
import kotlinx.android.synthetic.main.party_item.view.*

class PartyHolder(v: View) : ViewHolder<Party>(v) {
    private var view: View = v

    override fun bind(item: Party) {
        view.party_item_title.text = item.name;
    }
}