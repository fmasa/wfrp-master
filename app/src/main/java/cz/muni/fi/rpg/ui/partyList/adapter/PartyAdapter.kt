package cz.muni.fi.rpg.ui.partyList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.model.domain.party.Party

class PartyAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: OnClickListener<Party>
) : ListAdapter<Party, PartyHolder>(
    object : DiffUtil.ItemCallback<Party>() {
        override fun areItemsTheSame(oldItem: Party, newItem: Party) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Party, newItem: Party) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyHolder {
        return PartyHolder(layoutInflater.inflate(R.layout.party_item, parent, false), onClickListener)
    }

    override fun onBindViewHolder(holder: PartyHolder, position: Int) {
        holder.bind(getItem(position))
    }
}