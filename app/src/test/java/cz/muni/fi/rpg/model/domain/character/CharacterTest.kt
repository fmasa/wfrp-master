package cz.muni.fi.rpg.model.domain.character

import org.junit.Assert.*
import cz.muni.fi.rpg.model.domain.common.Money
import org.junit.Test

class CharacterTest {
    private fun character() = Character(
        "Bilbo",
        "123",
        "Writer",
        "Noble",
        "Does not like orcs",
        "Food",
        Race.HALFLING,
        Stats(20, 40, 2, 4, 8, 5, 5, 4, 0, 10),
        Stats(20, 40, 2, 4, 8, 5, 5, 4, 0, 10),
        Points(0, 4, 4, 5, 5, 0, 0, 0, 0)
    )

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
}