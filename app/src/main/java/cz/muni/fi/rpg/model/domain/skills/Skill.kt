package cz.muni.fi.rpg.model.domain.skills

import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
data class Skill(
    @Contextual override val id: UUID,
    @Contextual override val compendiumId: UUID? = null,
    val advanced: Boolean,
    val characteristic: Characteristic,
    val name: String,
    val description: String,
    val advances: Int = 0
) : CharacterItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 2000
        const val MIN_ADVANCES = 0
    }

    init {
        require(name.isNotEmpty())
        require(advances >= MIN_ADVANCES)
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }
}
