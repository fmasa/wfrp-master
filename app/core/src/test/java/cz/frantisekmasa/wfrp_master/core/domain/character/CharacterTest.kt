package cz.frantisekmasa.wfrp_master.core.domain.character

import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CharacterTest {
    private fun character() = Character(
        name = "Bilbo",
        userId = "123",
        career = "Writer",
        socialClass = "Noble",
        status = SocialStatus(SocialStatus.Tier.GOLD, 2),
        psychology = "Does not like orcs",
        motivation = "Food",
        race = Race.HALFLING,
        characteristicsBase = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
        characteristicsAdvances = Stats(20, 40, 2, 4, 80, 5, 5, 4, 0, 10),
        points = Points(0, 4, 4, 5, 5, 0, 0, 0, 0, 0, 0)
    )

    private fun characterWithHardy() = character().apply {
        update(
            name = getName(),
            career = getCareer(),
            socialClass = getSocialClass(),
            race = Race.DWARF,
            status = SocialStatus(SocialStatus.Tier.BRASS, 2),
            characteristicsBase = getCharacteristicsBase(),
            characteristicsAdvances = getCharacteristicsAdvances(),
            maxWounds = getPoints().maxWounds,
            psychology = getPsychology(),
            motivation = getMotivation(),
            note = getNote(),
            hardyTalent = true,
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
        val character = character()

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
