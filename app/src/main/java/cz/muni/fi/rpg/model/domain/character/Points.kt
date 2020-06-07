package cz.muni.fi.rpg.model.domain.character

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
    val experience: Int
) {
    init {
        require(corruption >= 0)
        require(fate >= 0)
        require(fortune >= 0)
        require(resilience >= 0)
        require(resolve >= 0)
        require(wounds in 0..maxWounds)
        require(maxWounds > 0)
        require(experience >= 0)
    }

    fun updateMaxWounds(newMaxWounds: Int) = copy(
        maxWounds = newMaxWounds,
        wounds = min(newMaxWounds, wounds)
    )

    fun isHeavilyWounded() = wounds < 2;
}