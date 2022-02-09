package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import junit.framework.Assert.assertSame
import junit.framework.Assert.assertTrue
import org.junit.Test

class InitiativePlus1d10StrategyTest {
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
        // 10 (Initiative) + 8 (1d10) = 18
        val order1 = strategyWithFixedRoll(8)
            .determineInitiative(characteristics.copy(initiative = 10))

        // 12 (Initiative) + 5 (1d10) = 17
        val order2 = strategyWithFixedRoll(5)
            .determineInitiative(characteristics.copy(initiative = 12))

        assertTrue(order1 > order2)
    }

    @Test
    fun sameAdditionHasSameOrder() {
        // 10 (Initiative) + 8 (1d10) = 18
        val order1 = strategyWithFixedRoll(8)
            .determineInitiative(characteristics.copy(initiative = 10))

        // 12 (Initiative) + 6 (1d10) = 18
        val order2 = strategyWithFixedRoll(6)
            .determineInitiative(characteristics.copy(initiative = 12))

        assertSame(0, order1.compareTo(order2))
    }

    private fun strategyWithFixedRoll(rollValue: Int) =
        InitiativePlus1d10Strategy(FixedRandom(1..10, rollValue))
}
