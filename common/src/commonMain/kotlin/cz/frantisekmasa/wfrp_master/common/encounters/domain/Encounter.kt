package cz.frantisekmasa.wfrp_master.common.encounters.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Encounter(
    val id: UuidAsString,
    val name: String,
    val description: String,
    val position: Int,
    val completed: Boolean = false,
    val characters: Map<LocalCharacterId, Int> = emptyMap(),
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

    fun withCharacterCount(characterId: LocalCharacterId, count: Int): Encounter {
        return copy(
            characters = if (count < 1)
                characters - characterId
            else characters + mapOf(characterId to count)
        )
    }

    private fun validate(name: String, description: String) {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
    }

    fun changePosition(newPosition: Int) = copy(position = newPosition)
}
