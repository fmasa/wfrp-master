package cz.muni.fi.rpg.model.domain.character

import kotlin.math.min

data class Points(
   val insanity: Int,
   val fate: Int,
   val fortune: Int,
   val wounds: Int,
   val maxWounds: Int
) {
    init {
        require(insanity >= 0)
        require(fate >= 0)
        require(fortune in 0..fate)
        require(wounds in 0..maxWounds)
        require(maxWounds > 0)
    }

    fun updateMaxWounds(newMaxWounds: Int) = copy(
        maxWounds = newMaxWounds,
        wounds = min(newMaxWounds, wounds)
    )

    fun updateFate(newFate: Int) = copy(
        fate = newFate,
        fortune = min(newFate, fortune)
    )

    fun isHeavilyWounded() = wounds < 2;
}