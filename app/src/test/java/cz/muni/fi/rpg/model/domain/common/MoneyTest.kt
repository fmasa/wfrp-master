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
}