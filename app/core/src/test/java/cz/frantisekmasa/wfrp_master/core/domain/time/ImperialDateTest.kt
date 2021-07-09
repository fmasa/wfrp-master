package cz.frantisekmasa.wfrp_master.core.domain.time

import arrow.core.Left
import arrow.core.Right
import junit.framework.TestCase

class ImperialDateTest : TestCase("ImperialDateTest") {
    fun testFirstRegularDayOfCalendarIsMarktag() {
        assertSame(ImperialDate.DayOfWeek.MARKTAG, ImperialDate(1).dayOfWeek)
    }

    fun testMonth() {
        assertEquals(Right(1 to ImperialDate.Month.NACHEXEN), ImperialDate(1).day)
        assertEquals(Right(1 to ImperialDate.Month.JAHRDRUNG), ImperialDate(33).day)

        assertEquals(Left(ImperialDate.StandaloneDay.HEXENSTAG), ImperialDate(0).day)
        assertEquals(Left(ImperialDate.StandaloneDay.MITTERFRUHL), ImperialDate(66).day)
    }

    fun testDayOfWeek() {
        assertSame(null, ImperialDate(0).dayOfWeek)
        assertSame(ImperialDate.DayOfWeek.MARKTAG, ImperialDate(1).dayOfWeek)
        assertSame(
            ImperialDate.DayOfWeek.AUBENTAG,
            ImperialDate.of(1, 1, 9).removeDay().removeDay().dayOfWeek
        )
    }

    fun testCalendarIsZeroBased() {
        assertEquals(ImperialDate(0), ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 1))
        assertEquals(ImperialDate(ImperialDate.DAYS_IN_YEAR), ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 2))
    }

    fun testAdditions() {
        assertEquals(ImperialDate(8), ImperialDate(0).addWeek())
    }
}
