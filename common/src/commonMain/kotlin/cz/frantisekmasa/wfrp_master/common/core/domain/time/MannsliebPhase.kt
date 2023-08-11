package cz.frantisekmasa.wfrp_master.common.core.domain.time

import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class MannsliebPhase(
    override val translatableName: StringResource,
) : NamedEnum {
    NEW_MOON(Str.calendar_moon_phases_new_moon),
    FULL_MOON(Str.calendar_moon_phases_full_moon),
    WAXING(Str.calendar_moon_phases_waxing),
    WANING(Str.calendar_moon_phases_waning);

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
