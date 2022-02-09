package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import org.junit.Assert.assertTrue
import org.junit.Test

class InitiativeTestStrategyTest {
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
    fun greaterSuccessLevelHasHigherOrder() {
        // +1 SL
        val order1 = strategyWithFixedRolls(40).determineInitiative(characteristics.copy(initiative = 50))

        // +0 SL
        val order2 = strategyWithFixedRolls(20).determineInitiative(characteristics.copy(initiative = 20))

        assertTrue(
            "Combatant with +1 SL should have higher initiative order than combatant with +0 SL",
            order1 > order2,
        )
    }

    @Test
    fun combatantWithGreaterInitiativeHasHigherOrderWhenSuccessLevelsAreSame() {
        // +1 SL
        val order1 = strategyWithFixedRolls(40).determineInitiative(characteristics.copy(initiative = 50))

        // +1 SL
        val order2 = strategyWithFixedRolls(30).determineInitiative(characteristics.copy(initiative = 49))

        assertTrue(
            "Combatant with I = 50 should have higher initiative order than combatant with I = 49",
            order1 > order2,
        )
    }

    @Test
    fun orderIsSameWhenSuccessLevelsAndInitiativesAreSame() {
        // +1 SL
        val order1 = strategyWithFixedRolls(40).determineInitiative(characteristics)

        // +1 SL
        val order2 = strategyWithFixedRolls(41).determineInitiative(characteristics)

        assertTrue(
            "Combatant with I = 50 should have higher initiative order than combatant with I = 49",
            order1.compareTo(order2) == 0,
        )
    }

    private fun strategyWithFixedRolls(rollValue: Int) = InitiativeTestStrategy(
        FixedRandom(1..100, rollValue)
    )
}
