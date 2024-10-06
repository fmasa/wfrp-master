package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString

data class Injury(
    override val id: UuidAsString,
    override val name: String,
    override val isVisibleToPlayers: Boolean = true,
    val duration: String?,
    val possibleLocations: List<String>,
    val description: String,
): CompendiumItem<Injury>() {

    override fun replace(original: Injury) = copy(id = original.id)

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = !isVisibleToPlayers)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())
}
