package cz.frantisekmasa.wfrp_master.core.domain.time

import junit.framework.TestCase

class MannsliebPhaseTest : TestCase("MannsliebPhaseTest") {
    fun testWaningPeriod() {
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(1, 1, 1)))
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(12, 1, 1)))
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(5, 2, 1)))
    }

    fun testWaxingPeriod() {
        assertSame(MannsliebPhase.WAXING, MannsliebPhase.at(ImperialDate.of(14, 1, 1)))
        assertSame(MannsliebPhase.WAXING, MannsliebPhase.at(ImperialDate.of(7, 2, 1)))
    }

    fun testFullMoon() {
        assertSame(MannsliebPhase.FULL_MOON, MannsliebPhase.at(ImperialDate.of(ImperialDate.StandaloneDay.HEXENSTAG, 1)))
        assertSame(MannsliebPhase.FULL_MOON, MannsliebPhase.at(ImperialDate.of(18, 2, 1)))
    }

    fun testNewMoon() {
        assertSame(MannsliebPhase.NEW_MOON, MannsliebPhase.at(ImperialDate.of(13, 1, 1)))
        assertSame(MannsliebPhase.NEW_MOON, MannsliebPhase.at(ImperialDate.of(6, 2, 1)))
    }
}
