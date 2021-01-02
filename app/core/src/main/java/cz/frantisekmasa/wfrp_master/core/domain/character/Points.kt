package cz.frantisekmasa.wfrp_master.core.domain.character

import kotlin.math.min

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
        require(fortune in 0..fate)
        require(resilience >= 0)
        require(resolve in 0..resilience)
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
}