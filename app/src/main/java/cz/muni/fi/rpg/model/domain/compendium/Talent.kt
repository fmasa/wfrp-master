package cz.muni.fi.rpg.model.domain.compendium

import java.util.*

data class Talent(
    override val id: UUID,
    val name: String,
    val maxTimesTaken: String,
    val description: String,
): CompendiumItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val MAX_TIMES_TAKEN_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1500
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(maxTimesTaken.length <= MAX_TIMES_TAKEN_MAX_LENGTH) { "Maximum length of is $MAX_TIMES_TAKEN_MAX_LENGTH" }
    }
}