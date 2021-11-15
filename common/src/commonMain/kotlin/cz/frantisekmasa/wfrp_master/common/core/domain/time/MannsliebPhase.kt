package cz.frantisekmasa.wfrp_master.common.core.domain.time

enum class MannsliebPhase(val readableName: String) {
    NEW_MOON("New moon"),
    FULL_MOON("Full moon"),
    WAXING("Waxing"),
    WANING("Waning");

    companion object {
        private const val MANNSLIEB_WAXING_PERIOD = 13
        private const val MANNSLIEB_WANING_PERIOD = 12

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
