package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.core.domain.Stats
import org.junit.Assert.*
import org.junit.Test

class InitiativeCharacteristicStrategyTest {

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
    fun initiativeCharacteristicIsUsedByDefault() {
        val order1 =
            strategyWithFixedRolls(20).determineInitiative(characteristics.copy(initiative = 72))
        val order2 =
            strategyWithFixedRolls(10).determineInitiative(characteristics.copy(initiative = 71))

        assertSame(72, order1.toInt())
        assertSame(71, order2.toInt())

        assertTrue(
            "Combatant with I = 72 should have higher combat order than combatant with I = 71",
            order1 > order2,
        )
    }

    @Test
    fun agilityCharacteristicIsUsedWhenCombatantsHaveSameOrder() {
        val order1 =
            strategyWithFixedRolls(20).determineInitiative(characteristics.copy(agility = 72))
        val order2 =
            strategyWithFixedRolls(10).determineInitiative(characteristics.copy(agility = 71))

        // Users can still see only Initiative characteristic as Initiative order
        assertSame(characteristics.initiative, order1.toInt())
        assertSame(characteristics.initiative, order2.toInt())

        assertTrue(
            "Combatant with A = 72 should have higher combat order than combatant with A = 71",
            order1 > order2,
        )
    }

    @Test
    fun opposedAgilityTestIsUsedWhenCombatantsHaveSameOrder() {
        val order1 = strategyWithFixedRolls(20).determineInitiative(characteristics)
        val order2 = strategyWithFixedRolls(10).determineInitiative(characteristics)

        // Users can still see only Initiative characteristic as Initiative order
        assertSame(characteristics.initiative, order1.toInt())
        assertSame(characteristics.initiative, order2.toInt())

        assertTrue(
            "Combatant with +4 SL should have higher combat order than combatant with +3 SL",
            order1 < order2,
        )
    }

    @Test
    fun sameCharacteristicsAndRollTheSameTheyHaveSameOrder() {
        val order1 = strategyWithFixedRolls(20).determineInitiative(characteristics)
        val order2 = strategyWithFixedRolls(21).determineInitiative(characteristics)

        // Users can still see only Initiative characteristic as Initiative order
        assertSame(characteristics.initiative, order1.toInt())
        assertSame(characteristics.initiative, order2.toInt())

        assertTrue(
            "Combatant with +4 SL should have higher combat order than combatant with +3 SL",
            order1.compareTo(order2) == 0,
        )
    }

    private fun strategyWithFixedRolls(rollValue: Int) = InitiativeCharacteristicStrategy(
        FixedRandom(1..100, rollValue)
    )
}