package cz.frantisekmasa.wfrp_master.core.domain.time

import junit.framework.TestCase

class YearSeasonTest : TestCase("YearSeasonTest") {
    fun testSpringBounds() {
        assertSame(
            YearSeason.SPRING,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.NACHEXEN, 1))
        )
        assertSame(
            YearSeason.SPRING,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.SIGMARZEIT, 1))
        )
    }

    fun testSummerBounds() {
        assertSame(
            YearSeason.SUMMER,
            YearSeason.at(ImperialDate.of(18, ImperialDate.Month.SIGMARZEIT, 1))
        )
        assertSame(
            YearSeason.SUMMER,
            YearSeason.at(ImperialDate.of(16, ImperialDate.Month.NACHGEHEIM, 1))
        )
    }

    fun testFallBounds() {
        assertSame(
            YearSeason.FALL,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.NACHGEHEIM, 1))
        )
        assertSame(
            YearSeason.FALL,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.KALDEZEIT, 1))
        )
    }

    fun testWinterBounds() {
        assertSame(
            YearSeason.WINTER,
            YearSeason.at(ImperialDate.of(18, ImperialDate.Month.KALDEZEIT, 1))
        )
        assertSame(
            YearSeason.WINTER,
            YearSeason.at(ImperialDate.of(16, ImperialDate.Month.NACHEXEN, 1))
        )
    }
}