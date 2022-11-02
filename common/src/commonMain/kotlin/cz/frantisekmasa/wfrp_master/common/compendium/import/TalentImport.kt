package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class TalentImport(
    val name: String,
    val maxTimesTaken: String,
    val description: String,
) {
    init {
        require(name.isNotBlank()) { "Talent name cannot be blank" }
        name.requireMaxLength(Talent.NAME_MAX_LENGTH, "talent name")
        description.requireMaxLength(Talent.DESCRIPTION_MAX_LENGTH, "talent description")
    }

    fun toTalent() = Talent(
        id = uuid4(),
        name = name,
        maxTimesTaken = maxTimesTaken,
        description = description,
    )

    companion object {
        fun fromTalent(talent: Talent) = TalentImport(
            name = talent.name,
            maxTimesTaken = talent.maxTimesTaken,
            description = talent.description,
        )
    }
}
