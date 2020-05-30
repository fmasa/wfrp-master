package cz.muni.fi.rpg.model.domain.common

import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException

class MoneyTest {
    @Test
    fun twoInstancesWithSameAmountAreEqual() {
        assertEquals(Money.pennies(20), Money.pennies(20))
    }

    @Test
    fun additionProducesCorrectResult() {
        assertEquals(Money.pennies(50), Money.pennies(20) + Money.pennies(30))
    }

    @Test
    fun subtractionProducesCorrectResult() {
        assertEquals(Money.pennies(10), Money.pennies(40) - Money.pennies(30))
        assertEquals(Money.pennies(11), Money.shillings(1) - Money.pennies(1))
        assertEquals(Money.shillings(19), Money.crowns(1) - Money.shillings(1))
    }

    @Test
    fun moneyAreCorrectlyRepresentedAsDifferentUnits() {
        val amount = Money.crowns(20) + Money.shillings(19) + Money.pennies(11)

        assertSame(11, amount.getPennies())
        assertSame(19, amount.getShillings())
        assertSame(20, amount.getCrowns())
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeAmountThrowsException() {
        Money.pennies(-1)
    }

    /**
     * This may seem horrible from Money VO implementation standpoint,
     * but since this object is only used for in-game currency,
     * maximum limit of [Int.MAX_VALUE] is ok.
     *
     * Also note that this is huge amount of money,
     * that PCs should never be able to obtain (for comparison,
     * peasants in Warhammer Fantasy World earn a yearly wage of 9-to-15 Crowns).
     *
     * We don't know how to show this in UI anyway for now ¯\_(ツ)_/¯
     */
    @Test
    fun preventIntegerOverflow() {
        assertEquals(
            Money.pennies(Int.MAX_VALUE),
            Money.pennies(Int.MAX_VALUE) + Money.pennies(Int.MAX_VALUE)
        )
    }
}