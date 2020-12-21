package cz.muni.fi.rpg.model.domain.compendium

import cz.frantisekmasa.wfrp_master.core.common.requireMaxLength
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
        const val DESCRIPTION_MAX_LENGTH = 2000
    }

    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
    }

}