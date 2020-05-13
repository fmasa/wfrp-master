package cz.muni.fi.rpg.model.domain.skills

import java.util.UUID

data class Skill(
    val id: UUID,
    private val advanced: Boolean,
    private val characteristic: SkillCharacteristic,
    val name: String,
    private val description: String
) {
    init {
        require(name.isNotEmpty())
    }
}
