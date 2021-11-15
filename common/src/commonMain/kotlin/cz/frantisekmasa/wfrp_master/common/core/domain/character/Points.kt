package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
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

    enum class PointPool(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
        FATE({ it.labelFatePoints }),
        FORTUNE({ it.labelFortunePoints }),
        RESILIENCE({ it.labelResiliencePoints }),
        RESOLVE({ it.labelResolvePoints }),
    }
}
