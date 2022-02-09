package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

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

    private fun characterWithHardy() = with(character()) {
        update(
            name = name,
            career = career,
            socialClass = socialClass,
            race = Race.DWARF,
            status = SocialStatus(SocialStatus.Tier.BRASS, 2),
            characteristicsBase = characteristicsBase,
            characteristicsAdvances = characteristicsAdvances,
            maxWounds = points.maxWounds,
            psychology = psychology,
            motivation = motivation,
            note = note,
            hasHardyTalent = true,
        )
    }

    @Test
    fun `throws exception when trying to subtract more money than character has`() {
        val character = character()
            .addMoney(Money.pennies(15))

        assertFailsWith(NotEnoughMoney::class) {
            character.subtractMoney(Money.pennies(16))
        }
    }

    @Test
    fun `money are added to current balance`() {
        val character = character()
        val startingMoney = character.money

        assertEquals(
            startingMoney + Money.crowns(1) + Money.shillings(10),
            character
                .addMoney(Money.crowns(1))
                .addMoney(Money.shillings(10))
                .money
        )
    }

    @Test
    fun `cannot update points with wounds bonus not matching toughness bonus when hardy talent is enabled`() {
        val character = characterWithHardy()

        assertFailsWith(IllegalArgumentException::class) {
            character.updatePoints(character.points.copy(hardyWoundsBonus = 5))
        }
    }

    @Test
    fun `cannot update points with non-zero wounds bonus when hardy talent is not enabled`() {
        val character = character()

        assertFailsWith(IllegalArgumentException::class) {
            character.updatePoints(character.points.copy(hardyWoundsBonus = 5))
        }
    }

    @Test
    fun `current wounds are coerced to max wounds when max wounds are reduced below current wounds`() {
        val points = Points(0, 1, 1, 14, 12, 1, 1, 1, 1, 2, 2)

        val updatedPoints = points.withMaxWounds(4, 2)

        assertSame(6, updatedPoints.wounds)
    }
}
