package cz.muni.fi.rpg.ui.partyList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.ui.common.DiffCallback

class PartyAdapter(
    private val layoutInflater: LayoutInflater,
    private val userId: String,
    private val onClickListener: EntityListener<Party>,
    private val onRemoveListener: EntityListener<Party>
) : ListAdapter<Party, PartyHolder>(DiffCallback({ a, b -> a.id == b.id}, {a, b -> a == b})) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyHolder {
        return PartyHolder(
            layoutInflater.inflate(R.layout.party_item, parent, false),
            userId,
            onClickListener = onClickListener,
            onRemoveListener = onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: PartyHolder, position: Int) {
        holder.bind(getItem(position))
    }
}