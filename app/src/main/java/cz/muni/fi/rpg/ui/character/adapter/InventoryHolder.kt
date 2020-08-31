package cz.muni.fi.rpg.ui.character.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem

class InventoryHolder(
    private val view: View,
    private val onClickListener: EntityListener<InventoryItem>,
    private val onRemoveListener: EntityListener<InventoryItem>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: InventoryItem) {
        val description = view.findViewById<TextView>(R.id.inventoryItemDescription)
        view.findViewById<TextView>(R.id.inventoryItemName).text = item.name
        description.text = item.description

        if (item.quantity > 1) {
            val quantity = view.findViewById<TextView>(R.id.quantity)
            quantity.text = item.quantity.toString()
            quantity.visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.timesSymbol).visibility = View.VISIBLE
        }

        if (item.description.isBlank()) {
            description.visibility = View.GONE

            val inventoryItemLayout = view.findViewById<ConstraintLayout>(R.id.inventoryItemLayout)
            ConstraintSet().apply {
                clone(inventoryItemLayout)
                connect(
                    R.id.inventoryItemName,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(inventoryItemLayout)
            }
        }

        view.setOnClickListener { onClickListener(item) }

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(item)

                    false
                }
        }
    }
}