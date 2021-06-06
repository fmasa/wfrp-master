package cz.frantisekmasa.wfrp_master.compendium.domain

import cz.frantisekmasa.wfrp_master.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.core.domain.compendium.CompendiumItem
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Blessing(
    override val id: UUID,
    override val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
) : CompendiumItem {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1000
    }
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        range.requireMaxLength(RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(EFFECT_MAX_LENGTH, "effect")
    }
}