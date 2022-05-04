package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// TODO: Use value class
typealias InventoryItemId = Uuid

@Parcelize
@Serializable
@Immutable
data class InventoryItem(
    @Contextual override val id: InventoryItemId,
    val name: String,
    val description: String,
    val quantity: Int,
    val encumbrance: Encumbrance = Encumbrance.Zero,
    @Contextual val containerId: InventoryItemId? = null,
    val trappingType: TrappingType? = null,
) : CharacterItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    // TODO: Add support for Trappings compendium
    override val compendiumId: Uuid? get() = null

    val effectiveEncumbrance: Encumbrance get() {
        val type = trappingType

        if (containerId != null) {
            // Encumbrance of items carried in a container are ignored, see rulebook page 301
            return Encumbrance.Zero
        }

        if (type != null && type is TrappingType.WearableTrapping && type.worn) {
            // See rulebook page 293
            return encumbrance * quantity - Encumbrance.One
        }

        return encumbrance * quantity
    }

    fun duplicate(): InventoryItem = copy(
        id = uuid4(),
        name = duplicateName(name),
    )

    init {
        require(name.isNotBlank()) { "Inventory item must have non-blank name" }
        require(quantity > 0) { "Inventory item quantity must be > 0" }
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }
}