package cz.frantisekmasa.wfrp_master.common.core.domain.skills

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

@Parcelize
@Serializable
@Immutable
data class Skill(
    override val id: UuidAsString,
    override val compendiumId: UuidAsString? = null,
    val advanced: Boolean,
    val characteristic: Characteristic,
    val name: String,
    val description: String,
    val advances: Int = 0,
) : CharacterItem<Skill, CompendiumSkill> {
    init {
        require(name.isNotEmpty())
        require(advances >= MIN_ADVANCES)
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }

    override fun updateFromCompendium(compendiumItem: CompendiumSkill): Skill {
        return copy(
            advanced = compendiumItem.advanced,
            characteristic = compendiumItem.characteristic,
            name = compendiumItem.name,
            description = compendiumItem.description,
        )
    }

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    companion object {
        const val NAME_MAX_LENGTH = CompendiumSkill.NAME_MAX_LENGTH
        val DESCRIPTION_MAX_LENGTH get() = CompendiumSkill.DESCRIPTION_MAX_LENGTH
        const val MIN_ADVANCES = 0

        fun fromCompendium(
            compendiumSkill: CompendiumSkill,
            advances: Int,
        ): Skill {
            return Skill(
                id = uuid4(),
                compendiumId = compendiumSkill.id,
                advanced = compendiumSkill.advanced,
                characteristic = compendiumSkill.characteristic,
                name = compendiumSkill.name,
                description = compendiumSkill.description,
                advances = advances,
            )
        }
    }
}
