package cz.frantisekmasa.wfrp_master.common.core.domain.time

import arrow.core.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ImperialDateTest {

    @Test
    fun `first regular day of calendar is Marktag`() {
        assertSame(ImperialDate.DayOfWeek.MARKTAG, ImperialDate(1).dayOfWeek)
    }

    @Test
    fun `get month of date`() {
        assertEquals(Either.Right(1 to ImperialDate.Month.NACHEXEN), ImperialDate(1).day)
        assertEquals(Either.Right(1 to ImperialDate.Month.JAHRDRUNG), ImperialDate(33).day)

        assertEquals(Either.Left(ImperialDate.StandaloneDay.HEXENSTAG), ImperialDate(0).day)
        assertEquals(Either.Left(ImperialDate.StandaloneDay.MITTERFRUHL), ImperialDate(66).day)
    }

    @Test
    fun `day of week is correct`() {
        assertSame(null, ImperialDate(0).dayOfWeek)
        assertSame(ImperialDate.DayOfWeek.MARKTAG, ImperialDate(1).dayOfWeek)
        assertSame(
            ImperialDate.DayOfWeek.AUBENTAG,
            ImperialDate.of(1, 1, 9).removeDay().removeDay().dayOfWeek
        )
    }

    @Test
    fun `calendar date is zero-based`() {
        assertEquals(ImperialDate(0), ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 1))
        assertEquals(ImperialDate(ImperialDate.DAYS_IN_YEAR), ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 2))
    }

    @Test
    fun testAdditions() {
        assertEquals(ImperialDate(8), ImperialDate(0).addWeek())
    }
}
