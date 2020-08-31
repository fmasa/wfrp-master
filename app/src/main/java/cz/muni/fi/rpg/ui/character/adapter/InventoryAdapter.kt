package cz.muni.fi.rpg.ui.character.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.ui.common.DiffCallback

class InventoryAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<InventoryItem>,
    private val onRemoveListener: EntityListener<InventoryItem>
) : ListAdapter<InventoryItem, InventoryHolder>(DiffCallback({a, b -> a.id == b.id}, {a, b -> a == b})) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryHolder {
        return InventoryHolder(
            layoutInflater.inflate(R.layout.inventory_item, parent, false),
            onClickListener = onClickListener,
            onRemoveListener = onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: InventoryHolder, position: Int) {
        holder.bind(getItem(position))
    }
}