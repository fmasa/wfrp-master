package cz.muni.fi.rpg.ui.character.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import kotlinx.android.synthetic.main.inventory_item.view.*

class InventoryHolder(
    private val view: View,
    private val onRemoveListener: EntityListener<InventoryItem>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: InventoryItem) {
        view.inventoryItemName.text = item.name
        view.inventoryItemDescription.text = item.description
        view.inventoryItemDescription.visibility =
            if (item.description.isBlank()) View.GONE else View.VISIBLE

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(item)

                    false
                }
        }
    }
}