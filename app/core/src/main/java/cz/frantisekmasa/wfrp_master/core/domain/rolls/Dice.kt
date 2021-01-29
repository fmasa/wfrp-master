package cz.frantisekmasa.wfrp_master.core.domain.rolls

import kotlin.random.Random


class Dice(
    private val sides: Int,
    private val random: Random = Random,
) {
    init {
        require(sides > 0) { "Number of dice sides must be large than zero" }
    }

    fun companion() {

    }

    fun roll(): Int = if (sides == 1) 1 else random.nextInt(1, sides + 1)
}
