package cz.frantisekmasa.wfrp_master.compendium.domain

import cz.frantisekmasa.wfrp_master.core.domain.compendium.CompendiumItem
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Spell(
    override val id: UUID,
    override val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val castingNumber: Int,
    val effect: String,
    val lore: String,
) : CompendiumItem<Spell>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1000
        const val LORE_MAX_LENGTH = 50
    }

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(name.length <= NAME_MAX_LENGTH) { "Name must be shorter than $NAME_MAX_LENGTH" }
        require(range.length <= RANGE_MAX_LENGTH) { "Range must be shorter than $RANGE_MAX_LENGTH" }
        require(target.length <= TARGET_MAX_LENGTH) { "Target must be shorter than $TARGET_MAX_LENGTH" }
        require(duration.length <= DURATION_MAX_LENGTH) { "Duration must be shorter than $DURATION_MAX_LENGTH" }
        require(effect.length <= EFFECT_MAX_LENGTH) { "Effect must be shorter than $EFFECT_MAX_LENGTH" }
        require(lore.length <= LORE_MAX_LENGTH) { "LORE must be shorter than $LORE_MAX_LENGTH" }
    }

    override fun duplicate() = copy(id = UUID.randomUUID(), name = duplicateName())
}
