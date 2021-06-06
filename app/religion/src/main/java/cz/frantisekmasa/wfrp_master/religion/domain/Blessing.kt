package cz.frantisekmasa.wfrp_master.religion.domain

import cz.frantisekmasa.wfrp_master.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing as CompendiumBlessing
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
internal data class Blessing(
    override val id: UUID,
    override val compendiumId: UUID?,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
) : CharacterItem {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(CompendiumBlessing.NAME_MAX_LENGTH, "name")
        range.requireMaxLength(CompendiumBlessing.RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(CompendiumBlessing.TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(CompendiumBlessing.DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(CompendiumBlessing.EFFECT_MAX_LENGTH, "effect")
    }
}