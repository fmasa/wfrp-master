package cz.frantisekmasa.wfrp_master.religion.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing as CompendiumBlessing

@Parcelize
@Serializable
@Immutable
internal data class Blessing(
    @Contextual override val id: UUID,
    @Contextual override val compendiumId: UUID?,
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

    companion object {
        fun fromCompendium(blessing: CompendiumBlessing): Blessing = Blessing(
            id = UUID.randomUUID(),
            compendiumId = blessing.id,
            name = blessing.name,
            range = blessing.range,
            target = blessing.target,
            duration = blessing.duration,
            effect = blessing.effect,
        )
    }
}
