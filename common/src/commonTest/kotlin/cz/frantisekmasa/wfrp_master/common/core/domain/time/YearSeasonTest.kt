package cz.frantisekmasa.wfrp_master.common.core.domain.time

import kotlin.test.Test
import kotlin.test.assertSame

class YearSeasonTest {

    @Test
    fun `spring bounds are part of spring`() {
        assertSame(
            YearSeason.SPRING,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.NACHEXEN, 1))
        )
        assertSame(
            YearSeason.SPRING,
            YearSeason.at(ImperialDate.of(17, ImperialDate.Month.SIGMARZEIT, 1))
        )
    }

    @Test
    fun `summer bounds are part of summer`() {
        assertSame(
            YearSeason.SUMMER,
            YearSeason.at(ImperialDate.of(18, ImperialDate.Month.SIGMARZEIT, 1))
        )
        assertSame(
            YearSeason.SUMMER,
            YearSeason.at(ImperialDate.of(16, ImperialDate.Month.NACHGEHEIM, 1))
        )
    }

    @Test
    fun `fall bounds are part of fall`() {
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
