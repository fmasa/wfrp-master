package cz.muni.fi.rpg.ui.character.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem

class InventoryAdapter(
    private val layoutInflater: LayoutInflater,
    private val onRemoveListener: EntityListener<InventoryItem>
) : ListAdapter<InventoryItem, InventoryHolder>(
    object : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryHolder {
        return InventoryHolder(
            layoutInflater.inflate(R.layout.inventory_item, parent, false),
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: InventoryHolder, position: Int) {
        holder.bind(getItem(position))
    }
}