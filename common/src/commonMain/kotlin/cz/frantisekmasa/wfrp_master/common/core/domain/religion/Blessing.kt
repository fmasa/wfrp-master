package cz.frantisekmasa.wfrp_master.common.core.domain.religion

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing as CompendiumBlessing

@Parcelize
@Serializable
@Immutable
data class Blessing(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid?,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
) : CharacterItem<Blessing, CompendiumBlessing> {

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(CompendiumBlessing.NAME_MAX_LENGTH, "name")
        range.requireMaxLength(CompendiumBlessing.RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(CompendiumBlessing.TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(CompendiumBlessing.DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(CompendiumBlessing.EFFECT_MAX_LENGTH, "effect")
    }

    override fun updateFromCompendium(compendiumItem: CompendiumBlessing): Blessing {
        return copy(
            name = compendiumItem.name,
            range = compendiumItem.range,
            target = compendiumItem.target,
            duration = compendiumItem.duration,
            effect = compendiumItem.effect,
        )
    }

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    companion object {
        fun fromCompendium(blessing: cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing): Blessing = Blessing(
            id = uuid4(),
            compendiumId = blessing.id,
            name = blessing.name,
            range = blessing.range,
            target = blessing.target,
            duration = blessing.duration,
            effect = blessing.effect,
        )
    }
}
