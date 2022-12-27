package cz.frantisekmasa.wfrp_master.common.core.domain.religion

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle as CompendiumMiracle

@Parcelize
@Serializable
@Immutable
data class Miracle(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid?,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
    val cultName: String,
) : CharacterItem<Miracle, CompendiumMiracle> {

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(CompendiumMiracle.NAME_MAX_LENGTH, "name")
        range.requireMaxLength(CompendiumMiracle.RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(CompendiumMiracle.TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(CompendiumMiracle.DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(CompendiumMiracle.EFFECT_MAX_LENGTH, "effect")
        cultName.requireMaxLength(CompendiumMiracle.CULT_NAME_MAX_LENGTH, "cultName")
    }

    override fun updateFromCompendium(compendiumItem: CompendiumMiracle): Miracle {
        return copy(
            name = compendiumItem.name,
            range = compendiumItem.range,
            target = compendiumItem.target,
            duration = compendiumItem.duration,
            effect = compendiumItem.effect,
            cultName = compendiumItem.cultName,
        )
    }

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    companion object {
        fun fromCompendium(miracle: CompendiumMiracle): Miracle = Miracle(
            uuid4(),
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
