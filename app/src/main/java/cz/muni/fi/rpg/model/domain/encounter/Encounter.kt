package cz.muni.fi.rpg.model.domain.encounter

import java.util.*

class Encounter(
    val id: UUID,
    name: String,
    description: String,
    position: Int
) {
    var name: String = name
        private set

    var description: String = description
        private set

    var position: Int = position
        set(value) {
            require(value >= 0)
            field = value
        }

    @Suppress("unused") // This will be introduced in UI in future
    var completed: Boolean = false
        private set

    companion object {
        const val NAME_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1000
    }

    init {
        validate(name, description)
    }

    fun update(name: String, description: String) {
        validate(name, description)

        this.name = name
        this.description = description
    }

    private fun validate(name: String, description: String) {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
    }
}