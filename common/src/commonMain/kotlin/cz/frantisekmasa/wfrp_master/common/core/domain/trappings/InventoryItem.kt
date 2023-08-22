package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
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
    @Contextual override val compendiumId: Uuid? = null,
) : CharacterItem<InventoryItem, Trapping> {

    init {
        require(quantity > 0) { "Quantity must be greater than 0" }
    }

    @Stable
    val effectiveEncumbrance: Encumbrance get() {
        if (containerId != null) {
            // Encumbrance of items carried in a container are ignored, see rulebook page 301
            return Encumbrance.Zero
        }

        val type = trappingType

        if (type != null && type is TrappingType.WearableTrapping && type.worn) {
            // See rulebook page 293
            return totalEncumbrance - Encumbrance.One
        }

        return totalEncumbrance
    }

    val totalEncumbrance: Encumbrance get() = encumbrance * quantity

    override fun updateFromCompendium(compendiumItem: Trapping): InventoryItem = copy(
        name = compendiumItem.name,
        encumbrance = compendiumItem.encumbrance,
        trappingType = trappingType?.updateFromCompendium(compendiumItem.trappingType)
            ?: TrappingType.fromCompendium(compendiumItem.trappingType)
    )

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    fun duplicate(): InventoryItem = copy(
        id = uuid4(),
        name = duplicateName(name),
    )

    fun addToContainer(containerId: InventoryItemId): InventoryItem {
        return when (trappingType) {
            is TrappingType.WearableTrapping -> copy(
                trappingType = trappingType.takeOff(),
                containerId = containerId,
            )
            is TrappingType.Weapon -> copy(
                trappingType = trappingType.unequip(),
                containerId = containerId,
            )
            else -> copy(containerId = containerId)
        }
    }

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 1000

        fun fromCompendium(compendiumItem: Trapping): InventoryItem {
            return InventoryItem(
                id = uuid4(),
                name = compendiumItem.name,
                encumbrance = compendiumItem.encumbrance,
                trappingType = TrappingType.fromCompendium(compendiumItem.trappingType),
                description = compendiumItem.description,
                quantity = 1,
                containerId = null,
                compendiumId = compendiumItem.id,
            )
        }
    }
}
