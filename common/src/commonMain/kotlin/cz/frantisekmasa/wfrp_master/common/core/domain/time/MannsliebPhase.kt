package cz.frantisekmasa.wfrp_master.common.core.domain.time

import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class MannsliebPhase(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
    NEW_MOON({ it.calendar.moonPhases.newMoon }),
    FULL_MOON({ it.calendar.moonPhases.fullMoon }),
    WAXING({ it.calendar.moonPhases.waxing }),
    WANING({ it.calendar.moonPhases.waning });

    companion object {
        private const val MANNSLIEB_WAXING_PERIOD = 13
        private const val MANNSLIEB_WANING_PERIOD = 12

        @Stable
        fun at(date: ImperialDate): MannsliebPhase {
            return when ((date.dayOfYear - 1) % (MANNSLIEB_WAXING_PERIOD + MANNSLIEB_WANING_PERIOD)) {
                0 -> FULL_MOON
                in 1..MANNSLIEB_WANING_PERIOD -> WANING
                MANNSLIEB_WANING_PERIOD + 1 -> NEW_MOON
                else -> WAXING
            }
        }
    }
}
