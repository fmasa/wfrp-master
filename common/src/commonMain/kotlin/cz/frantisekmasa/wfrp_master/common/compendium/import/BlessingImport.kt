package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class BlessingImport(
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
    val isVisibleToPlayers: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "Blessing name cannot be blank" }
        name.requireMaxLength(Blessing.NAME_MAX_LENGTH, "blessing name")
        range.requireMaxLength(Blessing.RANGE_MAX_LENGTH, "blessing range")
        target.requireMaxLength(Blessing.TARGET_MAX_LENGTH, "blessing target")
        duration.requireMaxLength(Blessing.DURATION_MAX_LENGTH, "blessing duration")
        effect.requireMaxLength(Blessing.EFFECT_MAX_LENGTH, "blessing effect")
    }

    fun toBlessing() = Blessing(
        id = uuid4(),
        name = name,
        range = range,
        target = target,
        duration = duration,
        effect = effect,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    companion object {
        fun fromBlessing(blessing: Blessing) = BlessingImport(
            name = blessing.name,
            range = blessing.range,
            target = blessing.target,
            duration = blessing.duration,
            effect = blessing.effect,
            isVisibleToPlayers = blessing.isVisibleToPlayers,
        )
    }
}
