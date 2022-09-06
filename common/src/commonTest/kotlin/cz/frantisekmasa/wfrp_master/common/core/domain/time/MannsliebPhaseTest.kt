package cz.frantisekmasa.wfrp_master.common.core.domain.time

import kotlin.test.Test
import kotlin.test.assertSame

class MannsliebPhaseTest {

    @Test
    fun `waning period`() {
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(1, 1, 1)))
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(12, 1, 1)))
        assertSame(MannsliebPhase.WANING, MannsliebPhase.at(ImperialDate.of(5, 2, 1)))
    }

    @Test
    fun `waxing period`() {
        assertSame(MannsliebPhase.WAXING, MannsliebPhase.at(ImperialDate.of(14, 1, 1)))
        assertSame(MannsliebPhase.WAXING, MannsliebPhase.at(ImperialDate.of(7, 2, 1)))
    }

    @Test
    fun `full moon`() {
        assertSame(
            MannsliebPhase.FULL_MOON,
            MannsliebPhase.at(
                ImperialDate.of(
                    ImperialDate.StandaloneDay.HEXENSTAG,
                    1
                )
            )
        )
        assertSame(MannsliebPhase.FULL_MOON, MannsliebPhase.at(ImperialDate.of(18, 2, 1)))
    }

    @Test
    fun `new moon`() {
        assertSame(MannsliebPhase.NEW_MOON, MannsliebPhase.at(ImperialDate.of(13, 1, 1)))
        assertSame(MannsliebPhase.NEW_MOON, MannsliebPhase.at(ImperialDate.of(6, 2, 1)))
    }
}
