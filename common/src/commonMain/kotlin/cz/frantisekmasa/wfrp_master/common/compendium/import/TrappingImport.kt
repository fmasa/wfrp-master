package cz.frantisekmasa.wfrp_master.common.compendium.import

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import kotlinx.serialization.Serializable

@Serializable
data class TrappingImport(
    val name: String,
    val trappingType: TrappingType?,
    val description: String,
    val encumbrance: Encumbrance,
    val availability: Availability,
    val price: Money,
    val isVisibleToPlayers: Boolean,
) {
    fun toTrapping(): Trapping {
        return Trapping(
            id = uuid4(),
            name = name,
            trappingType = trappingType,
            description = description,
            encumbrance = encumbrance,
            availability = availability,
            price = price,
            isVisibleToPlayers = isVisibleToPlayers,
        )
    }

    companion object {
        fun fromTrapping(trapping: Trapping): TrappingImport {
            return TrappingImport(
                name = trapping.name,
                trappingType = trapping.trappingType,
                description = trapping.description,
                encumbrance = trapping.encumbrance,
                availability = trapping.availability,
                price = trapping.price,
                isVisibleToPlayers = trapping.isVisibleToPlayers,
            )
        }
    }
}
