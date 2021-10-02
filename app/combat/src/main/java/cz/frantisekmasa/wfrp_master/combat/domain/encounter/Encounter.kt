package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Encounter(
    @Contextual val id: UUID,
    val name: String,
    val description: String,
    val position: Int,
    @Suppress("unused") // This will be introduced in UI in future
    val completed: Boolean = false,
) {
    companion object {
        const val NAME_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1000
    }

    init {
        require(position >= 0)
        validate(name, description)
    }

    fun update(name: String, description: String): Encounter {
        validate(name, description)

        return copy(
            name = name,
            description = description
        )
    }

    private fun validate(name: String, description: String) {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
    }

    fun changePosition(newPosition: Int) = copy(position = newPosition)
}
