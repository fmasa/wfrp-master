package cz.muni.fi.rpg.ui.character.adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
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

        if (item.description.isBlank()) {
            view.inventoryItemDescription.visibility = View.GONE

            ConstraintSet().apply {
                clone(view.inventoryItemLayout)
                connect(
                    R.id.inventoryItemName,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(view.inventoryItemLayout)
            }
        }

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(item)

                    false
                }
        }
    }
}