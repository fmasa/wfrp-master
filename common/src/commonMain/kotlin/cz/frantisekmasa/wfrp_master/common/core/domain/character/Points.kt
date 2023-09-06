package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable

@Serializable
data class Points(
    val corruption: Int,
    val fate: Int,
    val fortune: Int,
    val wounds: Int,
    val maxWounds: Int?,
    val resilience: Int,
    val resolve: Int,
    val sin: Int,
    val experience: Int,
    val spentExperience: Int = 0,
    @Deprecated("Hardy is calculated from stats automatically")
    val hardyWoundsBonus: Int = 0
) {
    init {
        // TODO: Ensure fortune & resolve is not negative
        require(corruption >= 0)
        require(fate >= 0)
        require(resilience >= 0)
        require(maxWounds == null || maxWounds >= 0)
        require(experience >= 0)
        require(spentExperience >= 0)
    }

    fun modify(pool: PointPool, value: Int): Result<Points> {
        return runCatching {
            when (pool) {
                PointPool.FATE -> copy(fate = fate + value)
                PointPool.FORTUNE -> copy(fortune = (fortune + value).coerceAtLeast(0))
                PointPool.RESILIENCE -> copy(resilience = resilience + value)
                PointPool.RESOLVE -> copy(resolve = (resolve + value).coerceAtLeast(0))
            }
        }
    }

    fun coerceWoundsAtMost(maxWounds: Int): Points {
        if (wounds <= maxWounds) {
            return this
        }

        return copy(wounds = maxWounds)
    }

    fun get(pool: PointPool): Int {
        return when (pool) {
            PointPool.FATE -> fate
            PointPool.FORTUNE -> fortune
            PointPool.RESILIENCE -> resilience
            PointPool.RESOLVE -> resolve
        }
    }

    enum class PointPool(
        override val translatableName: StringResource,
    ) : NamedEnum {
        FATE(Str.points_fate),
        FORTUNE(Str.points_fortune),
        RESILIENCE(Str.points_resilience),
        RESOLVE(Str.points_resolve),
    }
}
