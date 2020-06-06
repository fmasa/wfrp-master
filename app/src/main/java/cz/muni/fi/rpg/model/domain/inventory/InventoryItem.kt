package cz.muni.fi.rpg.model.domain.inventory

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

typealias InventoryItemId = UUID

@Parcelize
data class InventoryItem(
    val id: InventoryItemId,
    val name: String,
    val description: String,
    val quantity: Int
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    init {
        require(name.isNotBlank()) { "Inventory item must have non-blank name" };
        require(quantity > 0) { "Inventory item quantity must be > 0" }
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $DESCRIPTION_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }
}