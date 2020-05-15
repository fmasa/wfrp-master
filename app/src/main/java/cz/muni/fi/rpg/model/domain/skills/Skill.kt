package cz.muni.fi.rpg.model.domain.skills

import java.util.UUID

data class Skill(
    val id: UUID,
    private val advanced: Boolean,
    val characteristic: SkillCharacteristic,
    val name: String,
    val description: String
) {
    init {
        require(name.isNotEmpty())
    }
}
