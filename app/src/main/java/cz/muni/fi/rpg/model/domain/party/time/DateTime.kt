package cz.muni.fi.rpg.model.domain.party.time

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonUnwrapped
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateTime(
    @field:JsonUnwrapped
    val date: ImperialDate,
    private val minutes: Int
) : Parcelable {

    companion object {
        private fun toMinutes(time: TimeOfDay) = time.hour * 60 + time.minute
    }

    init {
        require(minutes in 0 until 60 * 24)
    }

    val time: TimeOfDay
        get() = TimeOfDay(minutes / 60, minutes % 60)

    @Parcelize
    data class TimeOfDay(val hour: Int, val minute: Int) : Parcelable {
        fun format() = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

        init {
            require(hour in 0 until 24)
            require(minute in 0 until 60)
        }
    }

    constructor(date: ImperialDate, time: TimeOfDay) : this(date, toMinutes(time))

    /**
     * This is necessary for deserialization
     */
    @JsonCreator
    constructor(imperialDate: Int, minutes: Int) : this(ImperialDate(imperialDate), minutes)

    fun withTime(time: TimeOfDay): DateTime = copy(minutes = toMinutes(time))
}
