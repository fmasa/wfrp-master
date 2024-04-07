package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.serialization.NullableSerializer
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

// TODO: Use value class
typealias InventoryItemId = UuidAsString

@Parcelize
@Serializable
@Immutable
data class InventoryItem(
    override val id: InventoryItemId,
    val name: String,
    val description: String,
    /**
     * Used as custom player note for trappings where description is taken from compendium.
     */
    val note: String = "",
    val quantity: Int,
    val encumbrance: Encumbrance = Encumbrance.Zero,
    val containerId: InventoryItemId? = null,
    @Serializable(with = NullableTrappingTypeSerializer::class)
    val trappingType: TrappingType? = null,
    val itemQualities: Set<ItemQuality> = emptySet(),
    val itemFlaws: Set<ItemFlaw> = emptySet(),
    override val compendiumId: UuidAsString? = null,
) : CharacterItem<InventoryItem, Trapping> {

    init {
        require(quantity > 0) { "Quantity must be greater than 0" }
        note.requireMaxLength(NOTE_MAX_LENGTH, "note")
    }

    @Stable
    val effectiveEncumbrance: Encumbrance get() {
        if (containerId != null) {
            // Encumbrance of items carried in a container are ignored, see rulebook page 301
            return Encumbrance.Zero
        }

        val type = trappingType

        if (type is TrappingType.Prosthetic && type.worn) {
            // See rulebook page 308
            return Encumbrance.Zero
        }

        if (type != null && type is TrappingType.WearableTrapping && type.worn) {
            // See rulebook page 293
            return totalEncumbrance - Encumbrance.One
        }

        return totalEncumbrance
    }

    val totalEncumbrance: Encumbrance get() = encumbrance * quantity

    override fun updateFromCompendium(compendiumItem: Trapping): InventoryItem = copy(
        name = compendiumItem.name,
        encumbrance = compendiumItem.encumbrance + encumbranceModifier(itemQualities, itemFlaws),
        trappingType = trappingType?.updateFromCompendium(compendiumItem.trappingType)
            ?: TrappingType.fromCompendium(compendiumItem.trappingType)
    )

    fun update(
        itemQualities: Set<ItemQuality>,
        itemFlaws: Set<ItemFlaw>,
        quantity: Int,
        note: String,
    ): InventoryItem = copy(
        encumbrance = encumbrance -
            encumbranceModifier(this.itemQualities, this.itemFlaws) +
            encumbranceModifier(this.itemQualities, this.itemFlaws),
        itemQualities = itemQualities,
        itemFlaws = itemFlaws,
        quantity = quantity,
        note = note,
    )

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    fun duplicate(): InventoryItem = copy(
        id = uuid4(),
        name = if (compendiumId == null) duplicateName(name) else name,
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
        const val NAME_MAX_LENGTH = Trapping.NAME_MAX_LENGTH
        const val DESCRIPTION_MAX_LENGTH = Trapping.DESCRIPTION_MAX_LENGTH
        const val NOTE_MAX_LENGTH = 500

        fun fromCompendium(
            compendiumItem: Trapping,
            itemQualities: Set<ItemQuality>,
            itemFlaws: Set<ItemFlaw>,
            quantity: Int,
            note: String,
        ): InventoryItem {
            return InventoryItem(
                id = uuid4(),
                name = compendiumItem.name,
                encumbrance = compendiumItem.encumbrance + encumbranceModifier(itemQualities, itemFlaws),
                itemQualities = itemQualities,
                itemFlaws = itemFlaws,
                trappingType = TrappingType.fromCompendium(compendiumItem.trappingType),
                description = compendiumItem.description,
                note = note,
                quantity = quantity,
                containerId = null,
                compendiumId = compendiumItem.id,
            )
        }

        private fun encumbranceModifier(
            itemQualities: Set<ItemQuality>,
            itemFlaws: Set<ItemFlaw>
        ): Encumbrance {
            var modifier = Encumbrance.Zero

            if (ItemFlaw.BULKY in itemFlaws) {
                modifier += Encumbrance.One
            }

            if (ItemQuality.LIGHTWEIGHT in itemQualities && modifier > Encumbrance.Zero) {
                modifier += Encumbrance.One
            }

            return modifier
        }
    }
}

private class NullableTrappingTypeSerializer :
    NullableSerializer<TrappingType>(TrappingType.serializer())
