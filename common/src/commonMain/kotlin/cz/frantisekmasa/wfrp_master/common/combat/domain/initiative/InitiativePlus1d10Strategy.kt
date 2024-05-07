package cz.frantisekmasa.wfrp_master.common.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import kotlin.random.Random

internal class InitiativePlus1d10Strategy(random: Random) : InitiativeStrategy {
    private val dice = Dice(10, random)

    override fun determineInitiative(characteristics: Stats) =
        InitiativeOrder(
            characteristics.initiative + dice.roll(),
        )
}
