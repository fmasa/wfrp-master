package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MiracleImport(
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
    val cultName: String,
    val isVisibleToPlayers: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "Miracle name cannot be blank" }
        name.requireMaxLength(Miracle.NAME_MAX_LENGTH, "miracle name")
        range.requireMaxLength(Miracle.RANGE_MAX_LENGTH, "miracle range")
        target.requireMaxLength(Miracle.TARGET_MAX_LENGTH, "miracle target")
        duration.requireMaxLength(Miracle.DURATION_MAX_LENGTH, "miracle duration")
        effect.requireMaxLength(Miracle.EFFECT_MAX_LENGTH, "miracle effect")
        cultName.requireMaxLength(Miracle.CULT_NAME_MAX_LENGTH, "miracle cultName")
    }

    fun toMiracle() =
        Miracle(
            id = uuid4(),
            name = name,
            range = range,
            target = target,
            duration = duration,
            effect = effect,
            cultName = cultName,
            isVisibleToPlayers = isVisibleToPlayers,
        )

    companion object {
        fun fromMiracle(miracle: Miracle) =
            MiracleImport(
                name = miracle.name,
                range = miracle.range,
                target = miracle.target,
                duration = miracle.duration,
                effect = miracle.effect,
                cultName = miracle.cultName,
                isVisibleToPlayers = miracle.isVisibleToPlayers,
            )
    }
}
