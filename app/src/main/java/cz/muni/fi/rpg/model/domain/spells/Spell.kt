package cz.muni.fi.rpg.model.domain.spells

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
@Immutable
data class Spell(
    @Contextual override val id: UUID,
    @Contextual override val compendiumId: UUID? = null,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val castingNumber: Int,
    val effect: String,
    val memorized: Boolean = true, // TODO: Remove default value and migrate stored data
) : CharacterItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1000
    }

    val effectiveCastingNumber: Int get() = if (memorized) castingNumber else castingNumber * 2

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(name.length <= NAME_MAX_LENGTH) { "Name must be shorter than $NAME_MAX_LENGTH" }
        require(range.length <= RANGE_MAX_LENGTH) { "Range must be shorter than $RANGE_MAX_LENGTH" }
        require(target.length <= TARGET_MAX_LENGTH) { "Target must be shorter than $TARGET_MAX_LENGTH" }
        require(duration.length <= DURATION_MAX_LENGTH) { "Duration must be shorter than $DURATION_MAX_LENGTH" }
        require(effect.length <= EFFECT_MAX_LENGTH) { "Effect must be shorter than $EFFECT_MAX_LENGTH" }
    }
}
