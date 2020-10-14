package cz.muni.fi.rpg.model.domain.compendium

import cz.muni.fi.rpg.model.domain.compendium.common.Characteristic
import java.util.*

data class Skill(
    override val id: UUID,
    val name: String,
    val description: String,
    val characteristic: Characteristic,
    val advanced: Boolean,
) : CompendiumItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }
}