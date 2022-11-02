package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SkillImport(
    val name: String,
    val description: String,
    val characteristic: Characteristic,
    val advanced: Boolean,
) {
    init {
        require(name.isNotBlank()) { "Skill name cannot be blank" }
        name.requireMaxLength(Skill.NAME_MAX_LENGTH, "skill name")
        description.requireMaxLength(Skill.DESCRIPTION_MAX_LENGTH, "talent description")
    }

    fun toSkill() = Skill(
        id = uuid4(),
        name = name,
        description = description,
        characteristic = characteristic,
        advanced = advanced,
    )

    companion object {
        fun fromSkill(skill: Skill) = SkillImport(
            name = skill.name,
            description = skill.description,
            characteristic = skill.characteristic,
            advanced = skill.advanced,
        )
    }
}
