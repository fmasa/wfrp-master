package cz.muni.fi.rpg.model.domain.talents

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
@Immutable
data class Talent(
    @Contextual override val id: UUID,
    @Contextual override val compendiumId: UUID? = null,
    val name: String,
    val description: String,
    val taken: Int
) : CharacterItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 1500
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(taken in 1..999) { "Skill can be taken 1-100x" }
    }
}
