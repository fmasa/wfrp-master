package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Spell(
    override val id: UuidAsString,
    override val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val castingNumber: Int,
    val effect: String,
    // TODO: Remove this in favor of [lore] enum
    @SerialName("lore")
    val customLore: String,
    @SerialName("loreType")
    val lore: SpellLore? = null,
    override val isVisibleToPlayers: Boolean = true,
) : CompendiumItem<Spell>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1500
        const val LORE_MAX_LENGTH = 50
    }

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(castingNumber >= 0) { "Casting number cannot be negative" }
        require(name.length <= NAME_MAX_LENGTH) { "Name must be shorter than $NAME_MAX_LENGTH" }
        require(range.length <= RANGE_MAX_LENGTH) { "Range must be shorter than $RANGE_MAX_LENGTH" }
        require(target.length <= TARGET_MAX_LENGTH) { "Target must be shorter than $TARGET_MAX_LENGTH" }
        require(duration.length <= DURATION_MAX_LENGTH) { "Duration must be shorter than $DURATION_MAX_LENGTH" }
        require(effect.length <= EFFECT_MAX_LENGTH) { "Effect must be shorter than $EFFECT_MAX_LENGTH" }
        require(customLore.length <= LORE_MAX_LENGTH) { "LORE must be shorter than $LORE_MAX_LENGTH" }
    }

    override fun replace(original: Spell) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = isVisibleToPlayers)
}
