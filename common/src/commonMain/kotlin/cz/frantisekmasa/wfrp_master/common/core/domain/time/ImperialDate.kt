package cz.frantisekmasa.wfrp_master.common.core.domain.time

import androidx.compose.runtime.Immutable
import arrow.core.Either
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
@Parcelize
@Immutable
value class ImperialDate(
    private val imperialDay: Int
) : Comparable<ImperialDate>, Parcelable {

    init {
        require(imperialDay >= 0)
    }

    @Immutable
    enum class DayOfWeek(val readableName: String) {
        WELLENTAG("Wellentag"),
        AUBENTAG("Aubentag"),
        MARKTAG("Marktag"),
        BACKERTAG("Backertag"),
        BEZAHLTAG("Bezahltag"),
        KONISTAG("Konistag"),
        ANGESTAG("Angestag"),
        FESTAG("Festag"),
    }

    @Immutable
    enum class Month(
        val readableName: String,
        val standaloneDayAtTheBeginning: StandaloneDay? = null
    ) {
        NACHEXEN("Nachexen", StandaloneDay.HEXENSTAG),
        JAHRDRUNG("Jahrdrung"),
        PFLUGZEIT("Pflugzeit", StandaloneDay.MITTERFRUHL),
        SIGMARZEIT("Sigmarzeit"),
        SOMMERZEIT("Sommerzeit"),
        VORGEHEIM("Vorgeheim", StandaloneDay.SONNSTILL),
        NACHGEHEIM("Nachgeheim", StandaloneDay.GEHEIMNISTAG),
        ERNTEZEIT("Erntezeit"),
        BRAUZEIT("Brauzeit", StandaloneDay.MITTHERBST),
        KALDEZEIT("Kaldezeit"),
        ULRICZEIT("Ulriczeit"),
        VORHEXEN("Vorhexen", StandaloneDay.MONSTILLE);

        val numberOfDays: Int
            get() = when (this) {
                NACHEXEN, NACHGEHEIM -> 32
                else -> 33
            }
    }

    @Immutable
    enum class StandaloneDay(val readableName: String) {
        HEXENSTAG("Hexenstag"),
        MITTERFRUHL("Mitterfruhl"),
        SONNSTILL("Sonnstill"),
        GEHEIMNISTAG("Geheimnistag"),
        MITTHERBST("Mittherbst"),
        MONSTILLE("Mondstille"),
    }

    companion object {
        const val DAYS_IN_YEAR = 400
        private const val DAYS_IN_WEEK = 8

        fun of(day: StandaloneDay, year: Int): ImperialDate {
            require(year > 0) { "Year must be >= 1." }

            return ImperialDate(
                (year - 1) * DAYS_IN_YEAR +
                    Month.values()
                        .takeWhile { it.standaloneDayAtTheBeginning != day }
                        .map { it.numberOfDays + if (it.standaloneDayAtTheBeginning != null) 1 else 0 }
                        .sum()
            )
        }

        fun of(day: Int, month: Int, year: Int): ImperialDate {

            require(month in 1..Month.values().size) { "Invalid month" }

            val monthInYear = Month.values()[month - 1]

            require(day in 1..monthInYear.numberOfDays) { "$day is not valid day number of $month" }
            require(year > 0) { "Year must be >= 1." }

            val imperialDay = (year - 1) * DAYS_IN_YEAR +
                // Days in previous months
                Month.values().takeWhile { it != monthInYear }
                    .map { it.numberOfDays + if (it.standaloneDayAtTheBeginning != null) 1 else 0 }
                    .sum() +
                // Days in current month
                if (monthInYear.standaloneDayAtTheBeginning != null) day else day - 1

            return ImperialDate(imperialDay)
        }

        fun of(day: Int, month: Month, year: Int): ImperialDate = of(day, month.ordinal + 1, year)
    }

    val dayOfWeek: DayOfWeek?
        get() {
            return day.fold(
                { null },
                { (day, month) ->
                    val firstWeekDayOfYear =
                        (year - 1) * (DAYS_IN_YEAR - StandaloneDay.values().size) + DayOfWeek.MARKTAG.ordinal

                    val firstWeekDayOfMonth = Month.values()
                        .takeWhile { it < month }
                        .map { it.numberOfDays }
                        .sum() + firstWeekDayOfYear

                    DayOfWeek.values()[(firstWeekDayOfMonth + day - 1) % DayOfWeek.values().size]
                }
            )
        }

    val dayOfYear: Int get() = imperialDay % DAYS_IN_YEAR + 1
    val year: Int get() = imperialDay / DAYS_IN_YEAR + if (imperialDay < 0) -1 else 1

    fun removeDay() = addDays(-1)
    fun addWeek() = addDays(DAYS_IN_WEEK)

    override fun compareTo(other: ImperialDate) = imperialDay.compareTo(other.imperialDay)

    private fun addDays(days: Int) = ImperialDate(imperialDay + days)

    val day: Either<StandaloneDay, Pair<Int, Month>>
        get() {
            var day = dayOfYear

            for (month in Month.values()) {
                month.standaloneDayAtTheBeginning?.let {
                    if (day == 1) {
                        return Either.Left(it)
                    }

                    day--
                }

                if (day <= month.numberOfDays) {
                    return Either.Right(day to month)
                }

                day -= month.numberOfDays
            }

            error("Could not compute correct day")
        }

    fun format(): String {
        return day.fold(
            { "${it.readableName}, $year" },
            { "${it.first} ${it.second.readableName}, ${dayOfWeek?.readableName}, $year" }
        )
    }
}
