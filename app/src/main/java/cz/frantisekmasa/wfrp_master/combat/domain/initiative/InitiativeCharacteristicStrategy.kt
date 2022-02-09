package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.TestResult
import kotlin.random.Random

internal class InitiativeCharacteristicStrategy(random: Random) : InitiativeStrategy {

    private val dice = Dice(100, random)

    override fun determineInitiative(characteristics: Stats) = InitiativeOrder(
        characteristics.initiative,
        characteristics.agility,

        // Agility Opposed Test
        // There is a difference in rulebook. There character that wins chooses who goes first,
        // we use the winner as the one who goes first (otherwise it would mean that we have
        // to build UI for something that is essentially edge case)
        TestResult(dice.roll(), characteristics.agility).successLevel,
    )
}
