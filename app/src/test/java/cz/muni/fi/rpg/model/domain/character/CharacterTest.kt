package cz.muni.fi.rpg.model.domain.character

import org.junit.Assert.*
import cz.muni.fi.rpg.model.domain.common.Money
import org.junit.Test

class CharacterTest {
    private fun character() = Character(
        "Bilbo",
        "123",
        "Writer",
        Race.HALFLING,
        Stats(20, 40, 2, 4, 8, 5, 5, 4, 0),
        Points(0, 4, 4, 5, 5)
    )

    @Test
    fun testThrowsExceptionWhenTryingToGiveOutMoreMoneyThanCharacterHas() {
        val character = character()

        character.receiveMoney(Money.pennies(15))

        try {
            character.giveMoney(Money.pennies(16))
            fail("Expected ${NotEnoughMoney::class.simpleName} exception, but none was thrown")
        } catch (e: NotEnoughMoney) {
        }
    }

    @Test
    fun testReceivedMoneyAreAddedToCurrentBalance() {
        val character = character()
        val startingMoney = character.getMoney()

        character.receiveMoney(Money.crowns(1))
        character.receiveMoney(Money.shillings(10))

        assertEquals(
            startingMoney + Money.crowns(1) + Money.shillings(10),
            character.getMoney()
        )
    }
}