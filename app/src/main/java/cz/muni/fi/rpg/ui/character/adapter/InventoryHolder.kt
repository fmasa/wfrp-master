package cz.muni.fi.rpg.ui.character.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import kotlinx.android.synthetic.main.inventory_item.view.*

class InventoryHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {
    fun bind(item: InventoryItem) {
        view.inventoryItemName.text = item.name
        view.inventoryItemDescription.text = item.description
        view.inventoryItemQuantity.text = item.quantity.toString()
    }
}