package cz.frantisekmasa.wfrp_master.religion.domain

import cz.frantisekmasa.wfrp_master.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle as CompendiumMiracle

@Parcelize
@Serializable
internal data class Miracle(
    @Contextual override val id: UUID,
    @Contextual override val compendiumId: UUID?,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
    val cultName: String,
) : CharacterItem {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(CompendiumMiracle.NAME_MAX_LENGTH, "name")
        range.requireMaxLength(CompendiumMiracle.RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(CompendiumMiracle.TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(CompendiumMiracle.DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(CompendiumMiracle.EFFECT_MAX_LENGTH, "effect")
        cultName.requireMaxLength(CompendiumMiracle.CULT_NAME_MAX_LENGTH, "cultName")
    }

    companion object {
        fun fromCompendium(miracle: CompendiumMiracle): Miracle = Miracle(
            UUID.randomUUID(),
            compendiumId = miracle.id,
            name = miracle.name,
            range = miracle.range,
            target = miracle.target,
            duration = miracle.duration,
            effect = miracle.effect,
            cultName = miracle.cultName,
        )
    }
}
