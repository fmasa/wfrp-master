package cz.muni.fi.rpg.ui.partyList.adapter

import android.view.View
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.model.domain.party.Party
import kotlinx.android.synthetic.main.party_item.view.*

class PartyHolder(
    private val view: View,
    private val onClickListener: OnClickListener<Party>
) : ViewHolder<Party>(view) {
    override fun bind(item: Party) {
        view.party_item_title.text = item.name;

        view.setOnClickListener { onClickListener(item) };
    }
}