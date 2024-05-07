package cz.frantisekmasa.wfrp_master.common.core.domain.time

enum class YearSeason(val readableName: String) {
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter"),
    ;

    companion object {
        private const val FIRST_DAY_OF_SPRING = 17
        private const val FIRST_DAY_OF_SUMMER = 117
        private const val FIRST_DAY_OF_FALL = 217
        private const val FIRST_DAY_OF_WINTER = 317

        fun at(date: ImperialDate) =
            when (date.dayOfYear - 1) {
                in FIRST_DAY_OF_SPRING until FIRST_DAY_OF_SUMMER -> SPRING
                in FIRST_DAY_OF_SUMMER until FIRST_DAY_OF_FALL -> SUMMER
                in FIRST_DAY_OF_FALL until FIRST_DAY_OF_WINTER -> FALL
                else -> WINTER
            }
    }
}
