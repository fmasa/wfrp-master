package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString

data class Injury(
    override val id: UuidAsString,
    override val name: String,
    override val isVisibleToPlayers: Boolean = true,
    val duration: String,
    val possibleLocations: List<String>,
    val description: String,
): CompendiumItem<Injury>() {
    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        duration.requireMaxLength(DURATION_MAX_LENGTH, "duration")
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
    }

    override fun replace(original: Injury) = copy(id = original.id)

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = !isVisibleToPlayers)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 500
        const val DESCRIPTION_MAX_LENGTH = 1000
    }
}
