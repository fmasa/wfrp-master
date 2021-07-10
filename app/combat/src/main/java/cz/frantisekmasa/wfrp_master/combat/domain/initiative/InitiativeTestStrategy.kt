package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.core.domain.rolls.TestResult
import kotlin.random.Random

internal class InitiativeTestStrategy(random: Random) : InitiativeStrategy {
    private val dice = Dice(100, random)

    override fun determineInitiative(characteristics: Stats) = InitiativeOrder(
        TestResult(dice.roll(), characteristics.initiative).successLevel,
        characteristics.initiative,
    )
}
