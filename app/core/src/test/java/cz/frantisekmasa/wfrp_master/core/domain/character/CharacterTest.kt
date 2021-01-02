package cz.frantisekmasa.wfrp_master.core.domain.character

import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException

class CharacterTest {
    private fun character() = Character(
        "Bilbo",
        "123",
        "Writer",
        "Noble",
        "Does not like orcs",
        "Food",
        Race.HALFLING,
        Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
        Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
        Points(0, 4, 4, 5, 5, 0, 0, 0, 0, 0, 0)
    )

    private fun characterWithHardy() = character().apply {
        update(
            getName(),
            getCareer(),
            getSocialClass(),
            Race.DWARF,
            getCharacteristicsBase(),
            getCharacteristicsAdvances(),
            getPoints().maxWounds,
            getPsychology(),
            getMotivation(),
            getNote(),
            true
        )
    }

    @Test
    fun testThrowsExceptionWhenTryingToSubtractMoreMoneyThanCharacterHas() {
        val character = character()

        character.addMoney(Money.pennies(15))

        try {
            character.subtractMoney(Money.pennies(16))
            fail("Expected ${NotEnoughMoney::class.simpleName} exception, but none was thrown")
        } catch (e: NotEnoughMoney) {
        }
    }

    @Test
    fun testMoneyAreAddedToCurrentBalance() {
        val character = character()
        val startingMoney = character.getMoney()

        character.addMoney(Money.crowns(1))
        character.addMoney(Money.shillings(10))

        assertEquals(
            startingMoney + Money.crowns(1) + Money.shillings(10),
            character.getMoney()
        )
    }

    @Test
    fun testCannotUpdatePointsWithWoundsBonusNotMatchingToughnessBonusWhenHardyTalentIsEnabled() {
        val character = characterWithHardy()

        try {
            character.updatePoints(character.getPoints().copy(hardyWoundsBonus = 5))
            fail("Expected ${IllegalArgumentException::class.simpleName} exception, but none was thrown")
        } catch (e: IllegalArgumentException) {
        }
    }

    @Test
    fun testCannotUpdatePointsWithNonZeroWoundsBonusWhenHardyTalentIsNotEnabled() {
        val character =  character()

        try {
            character.updatePoints(character.getPoints().copy(hardyWoundsBonus = 5))
            fail("Expected ${IllegalArgumentException::class.simpleName} exception, but none was thrown")
        } catch (e: IllegalArgumentException) {
        }
    }

    @Test
    fun testWoundsAreReducedToMaxWoundsWhenReducingWounds() {
        val points = Points(0, 1, 1, 14, 12, 1, 1, 1, 1, 2, 2)

        val updatedPoints = points.withMaxWounds(4, 2)

        assertSame(6, updatedPoints.wounds)
    }
}