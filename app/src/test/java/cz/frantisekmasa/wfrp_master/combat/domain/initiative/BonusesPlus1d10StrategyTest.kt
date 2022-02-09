package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class BonusesPlus1d10StrategyTest {
    private val characteristics = Stats(
        weaponSkill = 50,
        dexterity = 50,
        ballisticSkill = 50,
        strength = 50,
        toughness = 50,
        agility = 50,
        intelligence = 50,
        initiative = 50,
        willPower = 50,
        fellowship = 50,
    )

    @Test
    fun higherAdditionHasHigherOrder() {
        // 5 (IB) + 3 (AgiB) + 6 (1d10) = 14
        val order1 = strategyWithFixedRoll(6)
            .determineInitiative(characteristics.copy(initiative = 54, agility = 35))

        // 3 (IB) + 5 (AgiB) + 5 (1d10) = 13
        val order2 = strategyWithFixedRoll(5)
            .determineInitiative(characteristics.copy(initiative = 34, agility = 55))

        assertTrue(order1 > order2)
    }

    @Test
    fun sameAdditionHasSameOrder() {
        // 5 (IB) + 3 (AgiB) + 6 (1d10) = 14
        val order1 = strategyWithFixedRoll(6)
            .determineInitiative(characteristics.copy(initiative = 54, agility = 35))

        // 3 (IB) + 4 (AgiB) + 7 (1d10) = 14
        val order2 = strategyWithFixedRoll(7)
            .determineInitiative(characteristics.copy(initiative = 34, agility = 40))

        assertSame(0, order1.compareTo(order2))
    }

    private fun strategyWithFixedRoll(rollValue: Int) =
        BonusesPlus1d10Strategy(FixedRandom(1..10, rollValue))
}
