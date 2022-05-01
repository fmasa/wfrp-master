package cz.frantisekmasa.wfrp_master.common.core.domain.talents

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Talent(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid? = null,
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
