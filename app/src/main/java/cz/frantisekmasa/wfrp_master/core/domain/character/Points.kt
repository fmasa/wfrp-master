package cz.frantisekmasa.wfrp_master.core.domain.character

import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class Points(
    val corruption: Int,
    val fate: Int,
    val fortune: Int,
    val wounds: Int,
    val maxWounds: Int,
    val resilience: Int,
    val resolve: Int,
    val sin: Int,
    val experience: Int,
    val spentExperience: Int = 0,
    val hardyWoundsBonus: Int
) {
    init {
        require(corruption >= 0)
        require(fate >= 0)
        require(resilience >= 0)
        require(wounds in 0..(maxWounds + hardyWoundsBonus))
        require(maxWounds > 0)
        require(experience >= 0)
        require(spentExperience >= 0)
        require(hardyWoundsBonus >= 0)
    }

    fun withMaxWounds(newMaxWounds: Int, hardyWoundsBonus: Int) = copy(
        maxWounds = newMaxWounds,
        hardyWoundsBonus = hardyWoundsBonus,
        wounds = min(hardyWoundsBonus + newMaxWounds, wounds)
    )

    fun withFate(newFate: Int) = copy(
        fate = newFate,
        fortune = min(fortune, newFate)
    )

    fun withResilience(newResilience: Int) = copy(
        resilience = newResilience,
        resolve = min(resolve, newResilience)
    )

    fun isHeavilyWounded() = wounds < 2

    fun modify(pool: PointPool, value: Int): Result<Points> {
        return runCatching {
            when (pool) {
                PointPool.FATE -> copy(fate = fate + value)
                PointPool.FORTUNE -> copy(fortune = fortune + value)
                PointPool.RESILIENCE -> copy(resilience = resilience + value)
                PointPool.RESOLVE -> copy(resolve = resolve + value)
            }
        }
    }

    fun get(pool: PointPool): Int {
        return when (pool) {
            PointPool.FATE -> fate
            PointPool.FORTUNE -> fortune
            PointPool.RESILIENCE -> resilience
            PointPool.RESOLVE -> resolve
        }
    }

    enum class PointPool(@StringRes override val nameRes: Int) : NamedEnum {
        FATE(R.string.label_fate_points),
        FORTUNE(R.string.label_fortune_points),
        RESILIENCE(R.string.label_resilience),
        RESOLVE(R.string.label_resolve),
    }
}
