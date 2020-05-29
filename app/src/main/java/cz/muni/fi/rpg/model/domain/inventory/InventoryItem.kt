package cz.muni.fi.rpg.model.domain.inventory

import java.util.*

typealias InventoryItemId = UUID

data class InventoryItem(
    val id: InventoryItemId,
    val name: String,
    val description: String,
    val quantity: Int
) {
    init {
        require(name.isNotBlank()) {"Inventory item must have non-blank name"};
        require(quantity > 0) {"Inventory item quantity must be > 0"}
    }
}