package cz.frantisekmasa.wfrp_master.core.domain.rolls

import kotlin.random.Random


class Dice(
    val sides: Int,
) {
    init {
        require(sides > 0) { "Number of dice sides must be large than zero" }
    }

    fun companion() {

    }

    fun roll(): Int = if (sides == 1) 1 else Random.nextInt(1, sides)
}
